package com.sghore.needtalk.component

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.ServiceInfo
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
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.ClientEvent
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.usecase.ConnectToHostUseCase
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.MainActivity
import com.sghore.needtalk.util.Constants
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

// TODO: .fix 포그라운드 서비스 멈추는 경우 발생
@AndroidEntryPoint
class ClientTimerService : LifecycleService() {
    @Inject
    lateinit var connectToHostUseCase: ConnectToHostUseCase

    @Inject
    lateinit var sendPayloadUseCase: SendPayloadUseCase

    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var sensorManager: SensorManager

    var timerCmInfo = MutableStateFlow(TimerCommunicateInfo())
    private val binder = LocalBinder()

    private var baseNotification: NotificationCompat.Builder? = null
    private var sensorListener: SensorEventListener2? = null
    private var timerJob: Job? = null
    private var participantInfoIndex = -1

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

    fun connectToHost(
        userEntity: UserEntity?,
        hostEndpointId: String,
        onOpenDialog: (DialogScreen) -> Unit,
        onError: (String) -> Unit
    ) =
        lifecycleScope.launch {
            connectToHostUseCase(
                userId = userEntity?.userId ?: "",
                endpointId = hostEndpointId
            ).collectLatest { event ->
                when (event) {
                    // host에게 데이터가 왔을 때
                    is ClientEvent.PayloadReceived -> {
                        val payloadTypeJson =
                            event.payload.asBytes()?.toString(Charset.defaultCharset())

                        if (payloadTypeJson != null) {
                            val payloadType = Json.decodeFromString(
                                PayloadType.serializer(),
                                payloadTypeJson
                            )

                            when (payloadType) {
                                is PayloadType.UpdateTimerCmInfo -> {
                                    val currentInfo = payloadType.timerCommunicateInfo
                                    if (currentInfo.participantInfoList.size
                                        != timerCmInfo.value.participantInfoList.size
                                    ) {
                                        currentInfo.participantInfoList
                                            .forEachIndexed { index, participantInfo ->
                                                if (participantInfo?.userEntity?.userId == userEntity?.userId)
                                                    participantInfoIndex = index
                                            }
                                    }

                                    timerCmInfo.update { currentInfo }
                                    manageTimerActionState(
                                        timerActionState = timerCmInfo.value.timerActionState,
                                        hostEndpointId = hostEndpointId,
                                        onOpenDialog = onOpenDialog
                                    )
                                }

                                is PayloadType.RejectJoin -> {
                                    onOpenDialog(
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
                        if (userEntity != null) {
                            val payloadType = PayloadType.ClientJoinTimer(userEntity)
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
                            stopSensor()
                            timerPause()

                            onOpenDialog(
                                DialogScreen.DialogWarning(
                                    message = "호스트와 연결이 끊어졌습니다.\n" +
                                            "진행되고 있는 타이머는 중단됩니다.",
                                    isError = true
                                )
                            )
                        }
                    }

                    is ClientEvent.ClientConnectionFailure -> {

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
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
        }
    }

    private fun startSensor(onReady: () -> Unit, hostEndpointId: String) {
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
                                .apply {
                                    set(participantInfoIndex, updateParticipantInfo)
                                }
                        )
                    }

                    val payloadType = PayloadType.ClientReady(isReady = true, participantInfoIndex)
                    val payloadTypeJson =
                        Json.encodeToString(PayloadType.serializer(), payloadType)

                    sendPayloadUseCase(
                        bytes = payloadTypeJson.toByteArray(),
                        endpointId = hostEndpointId,
                        onFailure = {

                        }
                    )
                } else if (eventZ < 7f && participantInfo?.isReady == true) { // 타이머가 동작이 되었으며, 기기가 들려진 경우
                    val updateParticipantInfo = participantInfo.copy(isReady = false)

                    timerCmInfo.update {
                        it.copy(
                            participantInfoList = it.participantInfoList.toMutableList()
                                .apply {
                                    set(participantInfoIndex, updateParticipantInfo)
                                }
                        )
                    }

                    val payloadType = PayloadType.ClientReady(isReady = false, participantInfoIndex)
                    val payloadTypeJson =
                        Json.encodeToString(PayloadType.serializer(), payloadType)

                    sendPayloadUseCase(
                        bytes = payloadTypeJson.toByteArray(),
                        endpointId = hostEndpointId,
                        onFailure = {

                        })
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

    private fun manageTimerActionState(
        timerActionState: TimerActionState,
        hostEndpointId: String,
        onOpenDialog: (DialogScreen) -> Unit
    ) {
        when (timerActionState) {
            is TimerActionState.TimerReady -> {
                if (sensorListener == null) {
                    onOpenDialog(DialogScreen.DialogTimerReady)
                    startSensor(
                        onReady = { onOpenDialog(DialogScreen.DialogDismiss) },
                        hostEndpointId = hostEndpointId
                    )
                }
            }

            is TimerActionState.TimerRunning, is TimerActionState.StopWatchRunning -> {
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

                            onNotifyFinished() // foreground로 동작 시 알림 업데이트

                            stopSensor()
                        }
                    },
                    isStopwatch = timerCmInfo.value.isStopWatch
                )
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
        fun getService(): ClientTimerService = this@ClientTimerService
    }
}