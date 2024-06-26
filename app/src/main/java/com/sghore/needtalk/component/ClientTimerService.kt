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
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sghore.needtalk.R
import com.sghore.needtalk.data.repository.ClientEvent
import com.sghore.needtalk.data.repository.NearByRepository
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.domain.model.PinnedTalkTopic
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.main.MainActivity
import com.sghore.needtalk.util.Constants
import com.sghore.needtalk.util.bitmapToByteArray
import com.sghore.needtalk.util.parseMinuteSecond
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import javax.inject.Inject

// TODO: . fix: 앱을 처음 실행한 상태에서 타이머를 백그라운드에서 타이머를 동작시킬 시 서로 연결이 끊기는 버그 발생
@AndroidEntryPoint
class ClientTimerService : LifecycleService() {
    @Inject
    lateinit var nearByRepository: NearByRepository

    @Inject
    lateinit var sendPayloadUseCase: SendPayloadUseCase

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var sensorManager: SensorManager

    var timerCmInfo = MutableStateFlow(TimerCommunicateInfo())
    private val binder = LocalBinder()

    private var baseNotification: NotificationCompat.Builder? = null
    private var timerJob: Job? = null
    private var participantInfoIndex = -1
    private var wakeLock: PowerManager.WakeLock? = null

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onDestroy() {
        timerPause()
        releaseWakeLock()

        super.onDestroy()
    }

    fun connectToHost(
        userData: UserData?,
        hostEndpointId: String,
        onRejectJoin: (DialogScreen) -> Unit,
        onError: (String) -> Unit
    ) =
        lifecycleScope.launch {
            // 호스트 연결
            nearByRepository.connectToHost(
                userId = userData?.userId ?: "",
                endpointId = hostEndpointId
            ).collectLatest { event ->
                when (event) {
                    is ClientEvent.PayloadReceived -> { // host에게 데이터가 왔을 때
                        val payloadTypeJson =
                            event.payload.asBytes()?.toString(Charset.defaultCharset())

                        if (payloadTypeJson != null) {
                            val payloadType = Json.decodeFromString(
                                PayloadType.serializer(),
                                payloadTypeJson
                            )

                            when (payloadType) { // 데이터 유형
                                is PayloadType.UpdateTimerCmInfo -> { // 타이머 정보 업데이트
                                    val currentInfo = payloadType.timerCommunicateInfo
                                    if (currentInfo.participantInfoList.size
                                        != timerCmInfo.value.participantInfoList.size
                                    ) { // 참가자 인원이 달라졌을 경우
                                        currentInfo.participantInfoList
                                            .forEachIndexed { index, participantInfo ->
                                                if (participantInfo?.userId == userData?.userId)
                                                    participantInfoIndex = index // 참가자들의 인덱스 저장
                                            }
                                    }

                                    timerCmInfo.update { currentInfo }
                                    manageTimerActionState(timerActionState = timerCmInfo.value.timerActionState)
                                }

                                is PayloadType.RejectJoin -> { // 참가 거부 됨
                                    onRejectJoin(
                                        DialogScreen.DialogWarning(
                                            payloadType.rejectMessage,
                                            isError = true,
                                            isReject = true
                                        )
                                    )
                                }

                                else -> {}
                            }
                        }
                    }

                    // 연결이 성공적으로 됨
                    is ClientEvent.SuccessConnect -> {
                        if (userData != null) { // 참가자에 대한 정보를 호스트에 전송
                            val participant = ParticipantInfo(
                                userId = userData.userId,
                                name = userData.name,
                                profileImage = bitmapToByteArray(userData.profileImage.asAndroidBitmap()),
                                experiencePoint = userData.experiencePoint,
                                friendshipPoint = userData.friendshipPoint,
                                endpointId = "",
                                isReady = null
                            )
                            val payloadType = PayloadType.ClientJoinTimer(participant)
                            val payloadTypeJson =
                                Json.encodeToString(PayloadType.serializer(), payloadType)

                            sendPayloadUseCase(
                                bytes = payloadTypeJson.toByteArray(),
                                endpointId = hostEndpointId,
                                onFailure = {

                                }
                            )
                        }
                    }

                    // host와 연결이 끊어졌을 때
                    is ClientEvent.Disconnect -> {
                        if (timerCmInfo.value.timerActionState != TimerActionState.TimerFinished) {
                            timerPause()
                            timerCmInfo.update {
                                it.copy(
                                    timerActionState = TimerActionState.TimerError(
                                        errorMsg = "호스트와 연결이 끊어져\n" +
                                                "타이머가 중단되었습니다."
                                    )
                                )
                            }

                            // 오류 알림
                            onNotifyWarning(
                                title = "타이머가 중단되었습니다.",
                                text = "호스트와 연결이 끊어져\n타이머가 중단되었습니다."
                            )
                        }
                    }

                    is ClientEvent.ClientConnectionFailure -> {

                    }

                    else -> {}
                }
            }
        }

    fun startForegroundService() {
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
        baseNotification =
            NotificationCompat.Builder(applicationContext, Constants.TIMER_SERVICE_CHANNEL)
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(actionPendingIntent)

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

        acquireWakeLock()
        ServiceCompat.startForeground(
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

    fun deviceFlip(isFlip: Boolean, hostEndpointId: String) {
        val participantInfo = timerCmInfo.value.participantInfoList[participantInfoIndex]
        val updateParticipantInfo = participantInfo?.copy(isReady = isFlip)

        timerCmInfo.update {
            it.copy(
                participantInfoList = timerCmInfo.value.participantInfoList.toMutableList()
                    .apply { set(participantInfoIndex, updateParticipantInfo) }
            )
        }

        val payloadType = PayloadType.ClientReady(isReady = isFlip, participantInfoIndex)
        val payloadTypeJson =
            Json.encodeToString(PayloadType.serializer(), payloadType)

        sendPayloadUseCase(
            bytes = payloadTypeJson.toByteArray(),
            endpointId = hostEndpointId,
            onFailure = {

            }
        )
    }

    private fun timerStartOrResume(
        startTime: Long,
        onUpdateTime: (Long) -> Unit,
        isStopwatch: Boolean
    ) {
        timerJob?.cancel()
        timerJob =
            lifecycleScope.launch(context = Dispatchers.Default) {
                var time = startTime
                var oldTimeMills = System.currentTimeMillis()

                if (isStopwatch) {
                    while (true) {
                        if (!isActive)
                            break

                        val delayMills = System.currentTimeMillis() - oldTimeMills
                        if (delayMills >= 1000L) {
                            time += 1000
                            oldTimeMills = System.currentTimeMillis()

                            onUpdateTime(time)
                        }
                    }
                } else {
                    while (time > 0L) {
                        if (!isActive)
                            break

                        val delayMills = System.currentTimeMillis() - oldTimeMills
                        if (delayMills >= 1000L) {
                            time -= 1000
                            oldTimeMills = System.currentTimeMillis()

                            onUpdateTime(time)
                        }
                    }
                }
            }
    }

    private fun timerPause() {
        timerJob?.cancel()
        timerJob = null
    }

    private fun manageTimerActionState(timerActionState: TimerActionState) {
        when (timerActionState) {
            is TimerActionState.TimerReady -> {}

            is TimerActionState.TimerRunning, is TimerActionState.StopWatchRunning -> {
                if (timerJob == null) {
                    timerStartOrResume(
                        startTime = timerCmInfo.value.currentTime,
                        onUpdateTime = { updateTime ->
                            if (updateTime != 0L) { // 타이머 동작 중
                                Log.i("Check", "time: $updateTime")
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

                                onNotifyWarning(
                                    text = "대화 타이머가 끝났어요.",
                                    title = "즐거운 대화가 되셨나요?\n설정한 타이머가 끝이났습니다."
                                ) // foreground로 동작 시 알림 업데이트
                            }
                        },
                        isStopwatch = timerCmInfo.value.isStopWatch
                    )
                }
            }

            is TimerActionState.TimerPause, is TimerActionState.StopWatchPause -> {
                timerPause()
                onNotifyUpdate(
                    parseMinuteSecond(
                        timerCmInfo.value.currentTime
                    ) + " (일시 정지)"
                )
            }

            else -> {}
        }
    }

    // 대화주제 고정
    fun pinnedTalkTopic(pinnedTalkTopic: PinnedTalkTopic?, hostEndpointId: String) {
        timerCmInfo.update { it.copy(pinnedTalkTopic = pinnedTalkTopic) }

        val payloadType = PayloadType.ClientPinnedTopic(pinnedTalkTopic)
        val payloadTypeJson =
            Json.encodeToString(PayloadType.serializer(), payloadType)

        sendPayloadUseCase(
            bytes = payloadTypeJson.toByteArray(),
            endpointId = hostEndpointId,
            onFailure = {

            }
        )
    }

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

    // 원할한 타이머 동작을 위한 WakeLock 설정
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
        fun getService(): ClientTimerService = this@ClientTimerService
    }
}