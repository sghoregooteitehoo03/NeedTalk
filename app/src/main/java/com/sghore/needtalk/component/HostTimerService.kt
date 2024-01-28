package com.sghore.needtalk.component

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sghore.needtalk.data.repository.ConnectionEvent
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.domain.usecase.StartAdvertisingUseCase
import dagger.hilt.android.AndroidEntryPoint
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

    private val binder = LocalBinder()
    private var timerCmInfo: TimerCommunicateInfo? = null

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    fun startAdvertising(
        initTimerCmInfo: TimerCommunicateInfo?,
        onUpdateUiState: (TimerCommunicateInfo?) -> Unit,
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