package com.sghore.needtalk.component

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sghore.needtalk.R
import com.sghore.needtalk.data.repository.ConnectionEvent
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.domain.usecase.StartAdvertisingUseCase
import com.sghore.needtalk.domain.usecase.StopAllConnectionUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.MainActivity
import com.sghore.needtalk.util.Constants
import com.sghore.needtalk.util.parseMinuteSecond
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import javax.inject.Inject

@AndroidEntryPoint
class HostTimerService : LifecycleService() {
    @Inject
    lateinit var startAdvertisingUseCase: StartAdvertisingUseCase

    @Inject
    lateinit var stopAllConnectionUseCase: StopAllConnectionUseCase

    @Inject
    lateinit var sendPayloadUseCase: SendPayloadUseCase

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var sensorManager: SensorManager

    var timerCmInfo = MutableStateFlow(TimerCommunicateInfo())
    private val binder = LocalBinder()

    private var timerJob: Job? = null
    private var baseNotification: NotificationCompat.Builder? = null
    private var sensorListener: SensorEventListener2? = null
    private var participantInfoIndex = 0

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onDestroy() {
        if (sensorListener != null) {
            stopSensor()
        }
        timerPause()

        super.onDestroy()
    }

    fun startAdvertising(
        initTimerCmInfo: TimerCommunicateInfo,
        onOpenDialog: (DialogScreen) -> Unit,
        onError: (String) -> Unit
    ) =
        lifecycleScope.launch {
            timerCmInfo.update { initTimerCmInfo }
            val packageName = applicationContext.packageName

            startAdvertisingUseCase(
                userId = initTimerCmInfo.participantInfoList[0]?.userEntity?.userId ?: "",
                packageName = packageName
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
                                    stopSensor()
                                    timerPause()

                                    onOpenDialog(
                                        DialogScreen.DialogWarning(
                                            message = "참여하고 있는 인원이 존재하지 않아\n" +
                                                    "타이머가 중단되었습니다.",
                                            isError = true
                                        )
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
                                            rejectMessage = "대화가 이미 시작되었습니다.",
                                            endpointId = event.endpointId,
                                            onFailure = {}
                                        )
                                    } else {
                                        val participantInfoList = currentInfo.participantInfoList
                                            .toMutableList()

                                        // 참가자 인원 리스트 추가
                                        participantInfoList.add(
                                            ParticipantInfo(
                                                userEntity = payloadType.user,
                                                endpointId = event.endpointId,
                                                isReady = null
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

                                is PayloadType.ClientReady -> {
                                    val currentInfo = timerCmInfo.value
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

                                    timerCmInfo.update {
                                        it.copy(participantInfoList = updateParticipantInfo)
                                    }
                                    isAvailableTimerStart()

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

    // TODO: .fix 포그라운드로 실행 시 타이머가 느려지는 경우 버그 확인
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
                .setAutoCancel(false)
                .setOngoing(true)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(actionPendingIntent)

        when (timerCmInfo.value.timerActionState) {
            is TimerActionState.TimerWaiting -> {
                baseNotification
                    ?.setContentTitle("인원 대기 중")
                    ?.setContentText("인원이 모일 때 가지 잠시 기다려주세요.")

                startForeground(Constants.NOTIFICATION_ID_TIMER, baseNotification!!.build())
            }

            is TimerActionState.TimerReady -> {
                baseNotification
                    ?.setContentTitle("대화를 시작해보세요.")
                    ?.setContentText(
                        "모든 사용자가 휴대폰을 내려놓으면\n" +
                                "타이머가 시작됩니다."
                    )

                startForeground(Constants.NOTIFICATION_ID_TIMER, baseNotification!!.build())
            }

            is TimerActionState.TimerRunning -> {
                baseNotification
                    ?.setContentTitle("대화에 집중하고 있습니다.")
                    ?.setContentText(parseMinuteSecond(timerCmInfo.value.currentTime))

                startForeground(Constants.NOTIFICATION_ID_TIMER, baseNotification!!.build())
            }

            is TimerActionState.TimerStop -> {
                baseNotification
                    ?.setContentTitle("대화에 집중하고 있습니다.")
                    ?.setContentText(
                        parseMinuteSecond(timerCmInfo.value.currentTime) +
                                " (일시 정지)"
                    )

                startForeground(Constants.NOTIFICATION_ID_TIMER, baseNotification!!.build())
            }

            else -> {}
        }
    }

    fun stopForegroundService() {
        if (baseNotification != null) {
            baseNotification = null
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

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
        startSensor(onReady = { onOpenDialog(DialogScreen.DialogDismiss) })
    }

    private fun startSensor(onReady: () -> Unit) {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        sensorListener = object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                val eventZ = event?.values?.get(2) ?: 0f
                val participantInfo = timerCmInfo.value.participantInfoList[participantInfoIndex]

                // 타이머가 동작되지 않았으며, 기기가 놓여져있는 경우
                if (eventZ > SensorManager.GRAVITY_EARTH * 0.95f && participantInfo?.isReady != true) {
                    val updateParticipantInfo = participantInfo?.copy(isReady = true)
                    onReady()
                    vibrate()

                    timerCmInfo.update {
                        it.copy(
                            participantInfoList = it.participantInfoList.toMutableList()
                                .apply { set(participantInfoIndex, updateParticipantInfo) }
                        )
                    }
                    isAvailableTimerStart()

                    sendUpdateTimerCmInfo(
                        updateTimerCmInfo = timerCmInfo.value,
                        onFailure = {}
                    )
                } else if (eventZ < 7f && participantInfo?.isReady == true) { // 타이머가 동작이 되었으며, 기기가 들려진 경우
                    val updateParticipantInfo = participantInfo.copy(isReady = false)
                    timerCmInfo.update {
                        it.copy(
                            participantInfoList = timerCmInfo.value.participantInfoList.toMutableList()
                                .apply { set(participantInfoIndex, updateParticipantInfo) }
                        )
                    }

                    isAvailableTimerStart()

                    sendUpdateTimerCmInfo(
                        updateTimerCmInfo = timerCmInfo.value,
                        onFailure = {}
                    )
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

            override fun onFlushCompleted(p0: Sensor?) {}
        }

        sensorManager.registerListener(
            sensorListener,
            sensor,
            SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun stopSensor() {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        sensorManager.unregisterListener(
            sensorListener,
            sensor,
        )
    }

    private fun timerStart(
        startTime: Long,
        onUpdateTime: (Long) -> Unit,
        isStopwatch: Boolean
    ) {
        timerJob?.cancel()
        timerJob = lifecycleScope.launch {
            var time = startTime
            if (isStopwatch) {
                while (true) {
                    delay(1000)

                    time += 1000L
                    onUpdateTime(time)
                }
            } else {
                while (time > 0L) {
                    delay(1000)

                    time -= 1000L
                    onUpdateTime(time)
                }
            }

            timerJob = null // 동작이 끝이 나면
        }
    }

    private fun timerPause() {
        timerJob?.cancel()
        timerJob = null
    }

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

    private fun isAvailableTimerStart() {
        val participantInfoList = timerCmInfo.value.participantInfoList
        val isStopwatch = timerCmInfo.value.isStopWatch

        if (participantInfoList.none { it?.isReady != true }) {
            val timerActionState =
                if (isStopwatch) TimerActionState.StopWatchRunning else TimerActionState.TimerRunning

            timerCmInfo.update {
                it.copy(timerActionState = timerActionState)
            }

            timerStart(
                startTime = timerCmInfo.value.currentTime,
                onUpdateTime = { updateTime ->
                    if (updateTime != 0L) { // 타이머 동작 중
                        // foreground로 동작 시 알림 업데이트
                        onNotifyUpdate(
                            parseMinuteSecond(
                                updateTime
                            )
                        )

                        timerCmInfo.update { it.copy(currentTime = updateTime) }
                    } else {
                        // 타이머 동작이 끝이난 경우
                        timerCmInfo.update {
                            it.copy(
                                currentTime = updateTime,
                                timerActionState = TimerActionState.TimerFinished
                            )
                        }

                        onNotifyFinished() // foreground로 동작 시 알림 업데이트

                        // 모든 동작 정지
                        stopSensor()
                    }
                },
                isStopwatch = isStopwatch
            )
        } else if (timerCmInfo.value.timerActionState == TimerActionState.TimerRunning ||
            timerCmInfo.value.timerActionState == TimerActionState.StopWatchRunning
        ) {
            val timerActionState = if (isStopwatch) {
                val isFinished = timerCmInfo.value.currentTime >= 60000
                TimerActionState.StopWatchStop(isFinished = isFinished)
            } else TimerActionState.TimerStop

            timerCmInfo.update { it.copy(timerActionState = timerActionState) }

            timerPause()
            onNotifyUpdate(
                parseMinuteSecond(timerCmInfo.value.currentTime)
                        + " (일시 정지)"
            )
        }
    }

    private fun onNotifyUpdate(
        contentText: String
    ) {
        if (baseNotification != null) { // foreground로 동작 시 알림 업데이트
            baseNotification?.setContentText(contentText)

            notificationManager.notify(
                Constants.NOTIFICATION_ID_TIMER,
                baseNotification?.build()
            )
        }
    }

    private fun onNotifyFinished() {
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
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(actionPendingIntent)
                    .setVibrate(longArrayOf(1000, 2000, 3000, 4000))
                    .setContentTitle("대화 타이머가 끝났어요.")
                    .setContentText("즐거운 대화가 되셨나요?\n설정한 타이머가 끝이났습니다.")

            notificationManager.notify(
                Constants.NOTIFICATION_ID_TIMER,
                baseNotification?.build()
            )
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = applicationContext.getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(200, 100))
        } else {
            vibrator.vibrate(200)
        }
    }

    inner class LocalBinder : Binder() {
        fun getService(): HostTimerService = this@HostTimerService
    }
}