package com.sghore.needtalk.component

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sghore.needtalk.R
import com.sghore.needtalk.data.repository.ConnectionEvent
import com.sghore.needtalk.data.repository.NearByRepository
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.domain.model.PinnedTalkTopic
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.domain.usecase.StopAllConnectionUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.main.MainActivity
import com.sghore.needtalk.util.Constants
import com.sghore.needtalk.util.parseMinuteSecond
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.charset.Charset
import javax.inject.Inject

// TODO:
//  . fix: 앱을 처음 실행한 상태에서 타이머를 백그라운드에서 타이머를 동작시킬 시 서로 연결이 끊기는 버그 발생
//  . fix: 백그라운드에서 녹음이 되지 않는 버그 존재
//  . test: 나중에 녹음 잘 되는지 확인(재생, 멈춤, 백그라운드)
@AndroidEntryPoint
class HostTimerService : LifecycleService() {
    @Inject
    lateinit var nearByRepository: NearByRepository

    @Inject
    lateinit var stopAllConnectionUseCase: StopAllConnectionUseCase

    @Inject
    lateinit var sendPayloadUseCase: SendPayloadUseCase

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var sensorManager: SensorManager

    val timerCmInfo = MutableStateFlow(TimerCommunicateInfo())
    val amplitudeFlow = MutableStateFlow(0)
    var outputFile: File? = null

    private val binder = LocalBinder()

    private var timerJob: Job? = null

    private var baseNotification: NotificationCompat.Builder? = null
    private var participantInfoIndex = 0
    private var wakeLock: PowerManager.WakeLock? = null
    private var audioRecorder: AudioRecorder? = null

    private var isFirst: Boolean = true

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onDestroy() {
        timerPause()
        stopRecording()
        releaseWakeLock()

        super.onDestroy()
    }

    fun startAdvertising(
        initTimerCmInfo: TimerCommunicateInfo,
        onError: (String) -> Unit
    ) =
        lifecycleScope.launch {
            timerCmInfo.update { initTimerCmInfo } // 타이머 정보 업데이트
            if (initTimerCmInfo.isAllowMic) { // 녹음을 허용하는 경우
                audioRecorder = AudioRecorder()
            }

            val packageName = applicationContext.packageName

            // 광고 수행
            nearByRepository.startAdvertising(
                userId = initTimerCmInfo.participantInfoList[0]?.userId ?: "",
                serviceId = packageName
            ).collectLatest { event ->
                when (event) {
                    // 기기간의 연결이 문제가 없는경우
                    is ConnectionEvent.SuccessConnect -> {
                        // 연결이 된 기기에게 타이머에 대한 정보를 전달 함
                        sendUpdateTimerCmInfo(
                            updateTimerCmInfo = timerCmInfo.value,
                            endpointId = event.endpointId,
                            onFailure = {
                                it.printStackTrace()
                                onError("")
                            }
                        )
                    }

                    // 어떤 기기와 연결이 끊어진 경우
                    is ConnectionEvent.Disconnected -> {
                        val currentInfo = timerCmInfo.value
                        // 타이머 동작이 끝난 상태가 아닌 경우
                        if (currentInfo.timerActionState != TimerActionState.TimerFinished) {
                            val participantInfoList =
                                currentInfo.participantInfoList.toMutableList()

                            val disconnectUser = participantInfoList.filter {
                                it?.endpointId == event.endpointId
                            }

                            if (disconnectUser.isNotEmpty()) {
                                // 참가자의 정보를 지움
                                participantInfoList.remove(disconnectUser[0])
                                timerCmInfo.update {
                                    it.copy(participantInfoList = participantInfoList)
                                }

                                // 대화가 시작이 되었고, 참여하는 인원이 호스트밖에 없을 시
                                if (
                                    participantInfoList.size == 1
                                    && currentInfo.timerActionState != TimerActionState.TimerWaiting
                                ) {
                                    timerPause()
                                    timerCmInfo.update {
                                        it.copy(
                                            timerActionState = TimerActionState.TimerError(
                                                errorMsg = "참여하고 있는 인원이 존재하지 않아\n" +
                                                        "타이머가 중단되었습니다."
                                            )
                                        )
                                    }

                                    onNotifyWarning(
                                        title = "타이머가 중단되었습니다.",
                                        text = "참여하고 있는 인원이 존재하지 않아\n타이머가 중단되었습니다."
                                    )
                                } else {
                                    // 지워진 정보를 업데이트 한 후 다른 기기들 갱신
                                    sendUpdateTimerCmInfo(
                                        updateTimerCmInfo = timerCmInfo.value,
                                        onFailure = {}
                                    )
                                }
                            }
                        }
                    }

                    // 다른 기기들로부터 데이터를 전달받음
                    is ConnectionEvent.PayloadReceived -> {
                        val payloadTypeJson =
                            event.payload.asBytes()?.toString(Charset.defaultCharset())

                        if (payloadTypeJson != null) {
                            // String 형태로 전달 된 데이터를 클래스로 전환
                            val payloadType = Json.decodeFromString(
                                PayloadType.serializer(),
                                payloadTypeJson
                            )

                            when (payloadType) {
                                // 다른 유저가 생성한 타이머에 참가하였을 때
                                is PayloadType.ClientJoinTimer -> {
                                    val currentInfo = timerCmInfo.value
                                    if (currentInfo.participantInfoList.size == currentInfo.maxMember) {
                                        // 참가자가 모두 참여한 상태에서 참가요청을 보내는 경우
                                        sendRejectMessage(
                                            rejectMessage = "인원이 모두 가득찼습니다.",
                                            endpointId = event.endpointId,
                                            onFailure = {}
                                        )
                                    } else if (currentInfo.timerActionState != TimerActionState.TimerWaiting) {
                                        // 타이머가 시작이 된 상태에서 참가요청이 온 경우
                                        sendRejectMessage(
                                            rejectMessage = "대화가 이미 진행 중 입니다.",
                                            endpointId = event.endpointId,
                                            onFailure = {}
                                        )
                                    } else {
                                        val participantInfoList = currentInfo.participantInfoList
                                            .toMutableList()

                                        // 참가자 인원 리스트 추가
                                        participantInfoList.add(
                                            payloadType.participant.copy(
                                                endpointId = event.endpointId
                                            )
                                        )
                                        // 인원이 추가된 데이터로 업데이트함
                                        timerCmInfo.update {
                                            it.copy(participantInfoList = participantInfoList)
                                        }

                                        // 업데이트 된 데이터를 참가자들에게 전달
                                        sendUpdateTimerCmInfo(
                                            updateTimerCmInfo = timerCmInfo.value,
                                            onFailure = {}
                                        )
                                    }
                                }

                                // 다른 유저들의 기기 기울임 여부에 대한 정보
                                is PayloadType.ClientReady -> {
                                    val currentInfo = timerCmInfo.value

                                    // 특정 유저의 기기 기울임 여부 설정
                                    val updateParticipantInfo = currentInfo.participantInfoList
                                        .toMutableList()
                                        .apply {
                                            set(
                                                payloadType.participantIndex,
                                                currentInfo.participantInfoList[payloadType.participantIndex]?.copy(
                                                    isReady = payloadType.isReady
                                                )
                                            )
                                        }

                                    // 정보 업데이트
                                    timerCmInfo.update {
                                        it.copy(participantInfoList = updateParticipantInfo)
                                    }
                                    isAvailableTimerStart() // 타이머 동작 가능한 지 확인

                                    sendUpdateTimerCmInfo(
                                        updateTimerCmInfo = timerCmInfo.value,
                                        onFailure = {}
                                    )
                                }

                                is PayloadType.ClientPinnedTopic -> {
                                    timerCmInfo.update {
                                        it.copy(pinnedTalkTopic = payloadType.talkTopic)
                                    }

                                    sendUpdateTimerCmInfo(
                                        updateTimerCmInfo = timerCmInfo.value,
                                        onFailure = {}
                                    )
                                }

                                else -> {}
                            }
                        }
                    }

                    else -> {}
                }
            }
        }

    // 포그라운드 서비스 시작
    fun startForegroundService() {
        // 알림 클릭 시 동작 이벤트(화면 표시)
        val actionPendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            Intent(applicationContext, MainActivity::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )

        // 기본 알림
        baseNotification =
            NotificationCompat.Builder(applicationContext, Constants.TIMER_SERVICE_CHANNEL)
                .setAutoCancel(true)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(actionPendingIntent)
                .setPriority(NotificationCompat.PRIORITY_LOW)

        // 타이머 상태에 따른 알림 내용 설정
        when (timerCmInfo.value.timerActionState) {
            is TimerActionState.TimerWaiting -> {
                baseNotification
                    ?.setContentTitle("인원 대기 중")
                    ?.setContentText("인원이 모일 때 가지 잠시 기다려주세요.")
            }

            is TimerActionState.TimerReady -> {
                baseNotification
                    ?.setContentTitle("대화를 시작해보세요.")
                    ?.setContentText(
                        "모든 사용자가 휴대폰을 내려놓으면\n" +
                                "타이머가 시작됩니다."
                    )
            }

            is TimerActionState.TimerRunning, is TimerActionState.StopWatchRunning -> {
                baseNotification
                    ?.setContentTitle("대화에 집중하고 있습니다.")
                    ?.setContentText(parseMinuteSecond(timerCmInfo.value.currentTime))
            }

            is TimerActionState.TimerPause, is TimerActionState.StopWatchPause -> {
                baseNotification
                    ?.setContentTitle("대화에 집중하고 있습니다.")
                    ?.setContentText(
                        parseMinuteSecond(timerCmInfo.value.currentTime) +
                                " (일시 정지)"
                    )
            }

            else -> {}
        }

        acquireWakeLock() // WakeLock 설정
        ServiceCompat.startForeground( // 포그라운드 서비스 시작
            this,
            Constants.NOTIFICATION_ID_TIMER,
            baseNotification!!.build(),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            } else {
                0
            }
        )
    }

    fun stopForegroundService() {
        if (baseNotification != null) {
            notificationManager.cancel(Constants.NOTIFICATION_ID_TIMER)
            baseNotification = null

            releaseWakeLock()
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        }
    }

    // 타이머 준비 동작
    fun timerReady(
        onOpenDialog: (DialogScreen) -> Unit
    ) {
        timerCmInfo.update {
            it.copy(timerActionState = TimerActionState.TimerReady)
        }

        sendUpdateTimerCmInfo(
            updateTimerCmInfo = timerCmInfo.value,
            onFailure = {}
        )

        onOpenDialog(DialogScreen.DialogTimerReady)
    }

    // 호스트 기기 기울임에 따른 타이머 동작 설정
    fun deviceFlip(isFlip: Boolean) {
        val participantInfo = timerCmInfo.value.participantInfoList[participantInfoIndex]
        val updateParticipantInfo = participantInfo?.copy(isReady = isFlip) // 호스트 유저에 대한 정보 업데이트

        timerCmInfo.update {
            it.copy(
                participantInfoList = timerCmInfo.value.participantInfoList.toMutableList()
                    .apply { set(participantInfoIndex, updateParticipantInfo) }
            )
        }
        isAvailableTimerStart() // 타이머가 동작 가능한 지 확인

        // 정보 업데이트
        sendUpdateTimerCmInfo(
            updateTimerCmInfo = timerCmInfo.value,
            onFailure = {}
        )
    }

    // 타이머 동작
    private fun timerStartOrResume(
        startTime: Long,
        onUpdateTime: (Long) -> Unit,
        isTimer: Boolean
    ) {
        timerJob?.cancel() // 기존에 타이머 동작이 있으면 취소하고 새로 시작
        timerJob =
            lifecycleScope.launch(context = Dispatchers.Default) {
                var time = startTime
                var oldTimeMills = System.currentTimeMillis()

                if (!isTimer) {
                    while (true) {
                        if (!isActive)
                            break

                        val delayMills = System.currentTimeMillis() - oldTimeMills
                        if (delayMills >= 1000L) { // 1초마다 동작
                            time += 60000
                            oldTimeMills = System.currentTimeMillis()

                            Log.i("CheckTime", "time: $time")
                            onUpdateTime(time)
                        }
                    }
                } else {
                    while (time > 0L) {
                        if (!isActive)
                            break

                        val delayMills = System.currentTimeMillis() - oldTimeMills
                        if (delayMills >= 1000L) { // 1초마다 동작
                            time -= 60000
                            oldTimeMills = System.currentTimeMillis()

                            Log.i("CheckTime", "time: $time")
                            onUpdateTime(time)
                        }
                    }
                }
            }
    }

    // 타이머 정지
    private fun timerPause() {
        timerJob?.cancel()
        timerJob = null
    }

    // 녹음 시작
    private fun startOrResumeRecording() {
        if (audioRecorder != null) {
            if (isFirst) { // 녹음 시작
                isFirst = false

                // 녹음 파일 경로 지정
                val tempDir = applicationContext.getExternalFilesDir("recordings")
                if (tempDir?.exists() == false) {
                    tempDir.mkdirs()
                }
                outputFile = File(tempDir, "record_${System.currentTimeMillis()}.pcm")

                audioRecorder!!.startRecording(
                    outputFile = outputFile!!,
                    amplitudeFlow = amplitudeFlow,
                    scope = lifecycleScope
                )
            } else { // 녹음 재시작
                audioRecorder!!.resumeRecording()
            }
        }
    }

    // 녹음 멈춤
    private fun pauseRecording() {
        audioRecorder?.pauseRecording()
    }

    // 녹음 끝내기
    private fun stopRecording() {
        if (!isFirst) {
            audioRecorder?.stopRecording()
        }
    }

    // 업데이트 된 타이머 정보를 특정 기기에게 전달
    private fun sendUpdateTimerCmInfo(
        updateTimerCmInfo: TimerCommunicateInfo?,
        endpointId: String,
        onFailure: (Exception) -> Unit
    ) {
        if (updateTimerCmInfo != null) {
            val sendPayloadType =
                PayloadType.UpdateTimerCmInfo(updateTimerCmInfo)
            val sendPayloadTypeJson =
                Json.encodeToString(
                    PayloadType.serializer(),
                    sendPayloadType
                )

            // 다른 기기에게 타이머에 대한 정보를 전달함
            sendPayloadUseCase(
                bytes = sendPayloadTypeJson.toByteArray(),
                endpointId = endpointId,
                onFailure = onFailure
            )
        }
    }

    // 업데이트 된 타이머 정보를 모든 기기에게 전달
    private fun sendUpdateTimerCmInfo(
        updateTimerCmInfo: TimerCommunicateInfo?,
        onFailure: (Exception) -> Unit
    ) {
        if (updateTimerCmInfo != null) {
            val sendPayloadType =
                PayloadType.UpdateTimerCmInfo(updateTimerCmInfo)
            val sendPayloadTypeJson =
                Json.encodeToString(
                    PayloadType.serializer(),
                    sendPayloadType
                )

            // 호스트와 연결된 모든 기기에게 타이머에 대한 정보를 전달함
            for (i in 1 until updateTimerCmInfo.participantInfoList.size) {
                val endpointId = updateTimerCmInfo.participantInfoList[i]?.endpointId ?: ""
                sendPayloadUseCase(
                    bytes = sendPayloadTypeJson.toByteArray(),
                    endpointId = endpointId,
                    onFailure = onFailure
                )
            }
        }
    }

    // 참가 거절 메세지
    private fun sendRejectMessage(
        rejectMessage: String,
        endpointId: String,
        onFailure: (Exception) -> Unit
    ) {
        val sendPayloadType = PayloadType.RejectJoin(rejectMessage)
        val sendPayloadTypeJson =
            Json.encodeToString(
                PayloadType.serializer(),
                sendPayloadType
            )

        sendPayloadUseCase(
            bytes = sendPayloadTypeJson.toByteArray(),
            endpointId = endpointId,
            onFailure = onFailure
        )
    }

    // 타이머가 동작 조건에 맞는지 확인
    private fun isAvailableTimerStart() {
        val participantInfoList = timerCmInfo.value.participantInfoList
        val isTimer = timerCmInfo.value.isTimer

        // 사용자가 기기를 모두 내려놓았는지 확인
        if (participantInfoList.none { it?.isReady != true }) {
            // 타이머 상태 업데이트
            val timerActionState =
                if (isTimer) TimerActionState.StopWatchRunning else TimerActionState.TimerRunning

            timerCmInfo.update {
                it.copy(timerActionState = timerActionState)
            }

            // 타이머 동작
            timerStartOrResume(
                startTime = timerCmInfo.value.currentTime,
                onUpdateTime = { updateTime ->
                    if (updateTime != 0L) { // 타이머 동작 중
                        timerCmInfo.update { it.copy(currentTime = updateTime) }

                        // foreground로 동작 시 알림 업데이트
                        onNotifyUpdate(parseMinuteSecond(updateTime))
                    } else { // 타이머 동작이 끝이난 경우
                        timerCmInfo.update {
                            it.copy(
                                currentTime = updateTime,
                                timerActionState = TimerActionState.TimerFinished
                            )
                        }

                        // foreground로 동작 시 알림 업데이트
                        onNotifyWarning(
                            text = "대화 타이머가 끝났어요.",
                            title = "즐거운 대화가 되셨나요?\n설정한 타이머가 끝이났습니다."
                        )
                    }
                },
                isTimer = isTimer
            )
            startOrResumeRecording() // 녹음 시작
        } else if (timerCmInfo.value.timerActionState == TimerActionState.TimerRunning ||
            timerCmInfo.value.timerActionState == TimerActionState.StopWatchRunning
        ) { // 타이머가 동작되는 도중 기기를 들어올린 유저가 존재하는 경우
            val timerActionState = if (!isTimer) {
                // 스톱워치 5분 이상 대화할 경우 끝내기 가능
                val isFinished = timerCmInfo.value.currentTime >= 300000
                TimerActionState.StopWatchPause(isFinished = isFinished)
            } else TimerActionState.TimerPause

            // 타이머 상태 업데이트
            timerCmInfo.update { it.copy(timerActionState = timerActionState) }

            timerPause() // 타이머 정지
            pauseRecording() // 녹음 정지
            onNotifyUpdate(
                parseMinuteSecond(timerCmInfo.value.currentTime)
                        + " (일시 정지)"
            )
        }
    }

    // 대화주제 고정
    fun pinnedTalkTopic(pinnedTalkTopic: PinnedTalkTopic?) {
        timerCmInfo.update { it.copy(pinnedTalkTopic = pinnedTalkTopic) }
        sendUpdateTimerCmInfo(timerCmInfo.value, onFailure = {})
    }

    fun getOutputFilePath() = outputFile?.absolutePath ?: ""

    // 알림 내용 업데이트
    private fun onNotifyUpdate(
        contentText: String
    ) {
        // foreground로 동작 시 알림 업데이트
        val updateNotification = baseNotification
            ?.setContentTitle("대화에 집중하고 있습니다.")
            ?.setContentText(contentText)
            ?.build()

        if (updateNotification != null) {
            notificationManager.notify(
                Constants.NOTIFICATION_ID_TIMER,
                updateNotification
            )
        }
    }

    // 오류 알림 표시
    private fun onNotifyWarning(title: String, text: String) {
        if (baseNotification != null) { // foreground로 동작 시 알림 업데이트
            val actionPendingIntent = PendingIntent.getActivity(
                applicationContext,
                0,
                Intent(
                    applicationContext,
                    MainActivity::class.java
                ),
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )

            baseNotification =
                NotificationCompat.Builder(
                    applicationContext,
                    Constants.DEFAULT_NOTIFY_CHANNEL
                )
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(actionPendingIntent)
                    .setContentTitle(title)
                    .setContentText(text)

            notificationManager.notify(
                Constants.NOTIFICATION_ID_TIMER,
                baseNotification?.build()
            )
        }
    }

    // Wakelock 동작 설정
    @SuppressLint("InvalidWakeLockTag", "WakelockTimeout")
    private fun acquireWakeLock() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            Constants.WAKE_LOCK_TAG
        )
        wakeLock?.acquire()
    }

    private fun releaseWakeLock() {
        wakeLock?.let {
            if (it.isHeld) {
                it.release()
            }
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): HostTimerService = this@HostTimerService
    }
}