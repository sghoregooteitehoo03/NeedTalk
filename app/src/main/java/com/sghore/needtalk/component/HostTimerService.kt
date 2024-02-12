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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import javax.inject.Inject

// TODO:
//  . NullPointer 처리
//  . flow를 이용하는 방법 생각해보기

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

    private val binder = LocalBinder()
    private var timerJob: Job? = null
    private var timerCmInfo: TimerCommunicateInfo? = null
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
        initTimerCmInfo: TimerCommunicateInfo?,
        onUpdateUiState: (TimerCommunicateInfo?) -> Unit,
        onOpenDialog: (DialogScreen) -> Unit,
        onError: (String) -> Unit
    ) =
        lifecycleScope.launch {
            timerCmInfo = initTimerCmInfo
            val packageName = applicationContext.packageName

            if (timerCmInfo != null) {
                startAdvertisingUseCase(
                    userId = timerCmInfo!!.participantInfoList[0]?.userEntity?.userId ?: "",
                    packageName = packageName
                ).collectLatest { event ->
                    when (event) {
                        // 기기간의 연결이 문제가 없는경우
                        is ConnectionEvent.SuccessConnect -> {
                            // 연결이 된 기기에게 타이머에 대한 정보를 전달 함
                            sendUpdateTimerCmInfo(
                                updateTimerCmInfo = timerCmInfo,
                                endpointId = event.endpointId,
                                onFailure = {
                                    it.printStackTrace()
                                    onError("")
                                }
                            )
                        }

                        // 어떤 기기와 연결이 끊어진 경우
                        is ConnectionEvent.Disconnected -> {
                            val timerState = timerCmInfo?.timerActionState
                            // 타이머 동작이 끝난 상태가 아닌 경우
                            if (timerState != TimerActionState.TimerFinished) {
                                val participantInfoList = timerCmInfo
                                    ?.participantInfoList
                                    ?.toMutableList()

                                val disconnectUser = participantInfoList?.filter {
                                    it?.endpointId == event.endpointId
                                }

                                if ((disconnectUser?.isNotEmpty()) == true) {
                                    // 참가자의 정보를 지움
                                    participantInfoList.remove(disconnectUser[0])
                                    timerCmInfo =
                                        timerCmInfo?.copy(participantInfoList = participantInfoList)
                                    onUpdateUiState(timerCmInfo)

                                    // 대화가 시작이 되었고, 참여하는 인원이 호스트밖에 없을 시
                                    if (
                                        participantInfoList.size == 1
                                        && timerState != TimerActionState.TimerWaiting
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
                                            updateTimerCmInfo = timerCmInfo,
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
                                        if (timerCmInfo?.participantInfoList?.size == timerCmInfo?.maxMember) {
                                            // 참가자가 모두 참여한 상태에서 참가요청을 보내는 경우
                                            sendRejectMessage(
                                                rejectMessage = "인원이 모두 가득찼습니다.",
                                                endpointId = event.endpointId,
                                                onFailure = {}
                                            )
                                        } else if (timerCmInfo?.timerActionState != TimerActionState.TimerWaiting) {
                                            // 타이머가 시작이 된 상태에서 참가요청이 온 경우
                                            sendRejectMessage(
                                                rejectMessage = "대화가 이미 시작되었습니다.",
                                                endpointId = event.endpointId,
                                                onFailure = {}
                                            )
                                        } else {
                                            val participantInfoList = timerCmInfo
                                                ?.participantInfoList
                                                ?.toMutableList()
                                                ?: mutableListOf()

                                            // 참가자 인원 리스트 추가
                                            participantInfoList.add(
                                                ParticipantInfo(
                                                    userEntity = payloadType.user,
                                                    endpointId = event.endpointId,
                                                    isReady = null
                                                )
                                            )
                                            // 인원이 추가된 데이터로 업데이트함
                                            timerCmInfo = timerCmInfo
                                                ?.copy(participantInfoList = participantInfoList)
                                            onUpdateUiState(timerCmInfo)

                                            // 업데이트 된 데이터를 참가자들에게 전달
                                            sendUpdateTimerCmInfo(
                                                updateTimerCmInfo = timerCmInfo,
                                                onFailure = {}
                                            )
                                        }
                                    }

                                    is PayloadType.ClientReady -> {
                                        val updateParticipantInfo = timerCmInfo?.participantInfoList
                                            ?.toMutableList()
                                            ?.apply {
                                                set(
                                                    payloadType.participantIndex,
                                                    timerCmInfo?.participantInfoList?.get(
                                                        payloadType.participantIndex
                                                    )?.copy(isReady = payloadType.isReady)
                                                )
                                            } ?: listOf()

                                        timerCmInfo =
                                            timerCmInfo?.copy(participantInfoList = updateParticipantInfo)
                                        isAvailableTimerStart(onUpdateUiState = onUpdateUiState)

                                        onUpdateUiState(timerCmInfo)
                                        sendUpdateTimerCmInfo(
                                            updateTimerCmInfo = timerCmInfo,
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

        when (timerCmInfo?.timerActionState) {
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
                    ?.setContentText(parseMinuteSecond(timerCmInfo?.currentTime ?: 0L))

                startForeground(Constants.NOTIFICATION_ID_TIMER, baseNotification!!.build())
            }

            is TimerActionState.TimerStop -> {
                baseNotification
                    ?.setContentTitle("대화에 집중하고 있습니다.")
                    ?.setContentText(
                        parseMinuteSecond(timerCmInfo?.currentTime ?: 0L) +
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
        onUpdateUiState: (TimerCommunicateInfo?) -> Unit,
        onOpenDialog: (DialogScreen) -> Unit
    ) {
        timerCmInfo = timerCmInfo?.copy(timerActionState = TimerActionState.TimerReady)
        onUpdateUiState(timerCmInfo)

        sendUpdateTimerCmInfo(
            updateTimerCmInfo = timerCmInfo,
            onFailure = {}
        )

        onOpenDialog(DialogScreen.DialogTimerReady)
        startSensor(
            onUpdateUiState = {
                val isReady = it?.participantInfoList?.get(participantInfoIndex)?.isReady
                if (isReady == true) {
                    onOpenDialog(DialogScreen.DialogDismiss)
                }

                onUpdateUiState(it)
            }
        )
    }

    private fun startSensor(
        onUpdateUiState: (TimerCommunicateInfo?) -> Unit
    ) {
        val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        sensorListener = object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                val eventZ = event?.values?.get(2) ?: 0f
                val participantInfo = timerCmInfo?.participantInfoList?.get(participantInfoIndex)

                // 타이머가 동작되지 않았으며, 기기가 놓여져있는 경우
                if (eventZ > SensorManager.GRAVITY_EARTH * 0.95f && participantInfo?.isReady != true) {
                    val updateParticipantInfo = participantInfo?.copy(isReady = true)

                    // TODO: 진동 기능 추가
                    timerCmInfo =
                        timerCmInfo?.copy(
                            participantInfoList = timerCmInfo?.participantInfoList?.toMutableList()
                                ?.apply { set(participantInfoIndex, updateParticipantInfo) }
                                ?: listOf()
                        )
                    isAvailableTimerStart(
                        onUpdateUiState = onUpdateUiState
                    )

                    onUpdateUiState(timerCmInfo)
                    sendUpdateTimerCmInfo(
                        updateTimerCmInfo = timerCmInfo,
                        onFailure = {}
                    )
                } else if (eventZ < 7f && participantInfo?.isReady == true) { // 타이머가 동작이 되었으며, 기기가 들려진 경우
                    val updateParticipantInfo = participantInfo.copy(isReady = false)
                    timerCmInfo = timerCmInfo
                        ?.copy(
                            participantInfoList = timerCmInfo?.participantInfoList?.toMutableList()
                                ?.apply { set(participantInfoIndex, updateParticipantInfo) }
                                ?: listOf()
                        )

                    isAvailableTimerStart(onUpdateUiState = onUpdateUiState)

                    onUpdateUiState(timerCmInfo)
                    sendUpdateTimerCmInfo(
                        updateTimerCmInfo = timerCmInfo,
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
        timerJob = lifecycleScope.launch {
            var time = startTime
            if (isStopwatch) {
                while (true) {
                    delay(1000)

                    // TODO: 테스트 값 집어넣은 상태 나중에 수정할 것
                    time += 10000L
                    onUpdateTime(time)
                }
            } else {
                while (time > 0L) {
                    delay(1000)

                    // TODO: 테스트 값 집어넣은 상태 나중에 수정할 것
                    time -= 10000L
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

    private fun isAvailableTimerStart(
        onUpdateUiState: (TimerCommunicateInfo?) -> Unit
    ) {
        val participantInfoList = timerCmInfo?.participantInfoList
        val isStopwatch = timerCmInfo?.isStopWatch ?: false

        if (participantInfoList?.none { it?.isReady != true } == true) {
            val timerActionState =
                if (isStopwatch) TimerActionState.StopWatchRunning else TimerActionState.TimerRunning

            timerCmInfo = timerCmInfo?.copy(timerActionState = timerActionState)

            timerStart(
                startTime = timerCmInfo?.currentTime ?: 0L,
                onUpdateTime = { updateTime ->
                    if (updateTime != 0L) { // 타이머 동작 중
                        timerCmInfo =
                            timerCmInfo?.copy(currentTime = updateTime)
                        onUpdateUiState(timerCmInfo)

                        // foreground로 동작 시 알림 업데이트
                        onNotifyUpdate(
                            parseMinuteSecond(
                                timerCmInfo?.currentTime ?: 0L
                            )
                        )
                    } else {
                        // 타이머 동작이 끝이난 경우
                        timerCmInfo = timerCmInfo?.copy(
                            currentTime = updateTime,
                            timerActionState = TimerActionState.TimerFinished
                        )

                        onUpdateUiState(timerCmInfo)
                        onNotifyFinished() // foreground로 동작 시 알림 업데이트

                        // 모든 동작 정지
                        stopSensor()
                    }
                },
                isStopwatch = isStopwatch
            )
        } else if (timerCmInfo?.timerActionState == TimerActionState.TimerRunning ||
            timerCmInfo?.timerActionState == TimerActionState.StopWatchRunning
        ) {
            val timerActionState = if (isStopwatch) {
                val isFinished = (timerCmInfo?.currentTime ?: 0L) >= 60000
                TimerActionState.StopWatchStop(isFinished = isFinished)
            } else TimerActionState.TimerStop

            timerCmInfo = timerCmInfo?.copy(timerActionState = timerActionState)

            timerPause()
            onNotifyUpdate(
                parseMinuteSecond(timerCmInfo?.currentTime ?: 0L)
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

    inner class LocalBinder : Binder() {
        fun getService(): HostTimerService = this@HostTimerService
    }
}