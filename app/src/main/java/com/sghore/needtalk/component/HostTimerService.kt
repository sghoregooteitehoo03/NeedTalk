package com.sghore.needtalk.component

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
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

@AndroidEntryPoint
class HostTimerService : LifecycleService() {
    @Inject
    lateinit var startAdvertisingUseCase: StartAdvertisingUseCase

    @Inject
    lateinit var sendPayloadUseCase: SendPayloadUseCase

    @Inject
    lateinit var notificationManager: NotificationManager

    private var timerJob: Job? = null
    private val binder = LocalBinder()
    private var timerCmInfo: TimerCommunicateInfo? = null
    private var baseNotification: NotificationCompat.Builder? = null

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    override fun onDestroy() {
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
                    userId = timerCmInfo!!.participantInfoList[0].userEntity.userId,
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
                            val participantInfoList = timerCmInfo
                                ?.participantInfoList
                                ?.toMutableList()

                            val disconnectUser = participantInfoList?.filter {
                                it.endpointId == event.endpointId
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
                                    && timerCmInfo?.timerActionState != TimerActionState.TimerWaiting
                                ) {
                                    timerStop()
                                    onOpenDialog(
                                        DialogScreen.DialogWarning(
                                            message = "참여하고 있는 인원이 존재하지 않아\n" +
                                                    "타이머가 중단되었습니다.",
                                            isError = true
                                        )
                                    )
                                } else {
                                    // 지워진 정보를 업데이트 한 후 다른 기기들 갱신
                                    for (i in 1 until participantInfoList.size) {
                                        sendUpdateTimerCmInfo(
                                            timerCmInfo,
                                            endpointId = participantInfoList[i].endpointId,
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
                                        val participantInfoList = timerCmInfo
                                            ?.participantInfoList
                                            ?.toMutableList()
                                            ?: mutableListOf()

                                        // 참가자 인원 리스트 추가
                                        participantInfoList.add(
                                            ParticipantInfo(
                                                userEntity = payloadType.user,
                                                endpointId = event.endpointId,
                                                isReady = false
                                            )
                                        )
                                        // 인원이 추가된 데이터로 업데이트함
                                        timerCmInfo = timerCmInfo
                                            ?.copy(participantInfoList = participantInfoList)
                                        onUpdateUiState(timerCmInfo)

                                        // 업데이트 된 데이터를 참가자들에게 전달
                                        for (i in 1 until participantInfoList.size) {
                                            sendUpdateTimerCmInfo(
                                                timerCmInfo,
                                                endpointId = participantInfoList[i].endpointId,
                                                onFailure = {}
                                            )
                                        }
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
                .setContentTitle("대화에 집중하고 있습니다.")
                .setContentText("00:00")

        startForeground(Constants.NOTIFICATION_ID_TIMER, baseNotification!!.build())
    }

    fun stopForegroundService() {
        if (baseNotification != null) {
            baseNotification = null
            stopForeground(STOP_FOREGROUND_REMOVE)
        }
    }

    fun runTimer(onUpdateUiState: (TimerCommunicateInfo?) -> Unit) {
        timerCmInfo = timerCmInfo?.copy(timerActionState = TimerActionState.TimerRunning)

        onUpdateUiState(timerCmInfo)
        for (i in 1 until (timerCmInfo?.participantInfoList?.size ?: 1)) {
            sendUpdateTimerCmInfo(
                updateTimerCmInfo = timerCmInfo,
                endpointId = timerCmInfo?.participantInfoList?.get(i)?.endpointId ?: "",
                onFailure = {}
            )
        }
        timerStart(onUpdateUiState)
    }

    fun timerStart(
        onUpdateUiState: (TimerCommunicateInfo?) -> Unit
    ) {
        if (timerCmInfo != null) {
            timerJob = lifecycleScope.launch {
                while ((timerCmInfo?.currentTime ?: 0L) > 0L) {
                    delay(1000)
                    timerCmInfo = timerCmInfo
                        ?.copy(currentTime = timerCmInfo?.currentTime?.minus(60000L) ?: 0L)

                    onUpdateUiState(timerCmInfo)
                    if (baseNotification != null) {
                        baseNotification!!.setContentText(
                            parseMinuteSecond(timerCmInfo?.currentTime ?: 0L)
                        )

                        notificationManager.notify(
                            Constants.NOTIFICATION_ID_TIMER,
                            baseNotification!!.build()
                        )
                    }
                }

                timerJob = null // 동작이 끝이 나면
            }
        }
    }

    fun timerStop() {
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

    inner class LocalBinder : Binder() {
        fun getService(): HostTimerService = this@HostTimerService
    }
}