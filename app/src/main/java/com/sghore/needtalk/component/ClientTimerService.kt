package com.sghore.needtalk.component

import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.ClientEvent
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.usecase.ConnectToHostUseCase
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import javax.inject.Inject

@AndroidEntryPoint
class ClientTimerService : LifecycleService() {
    @Inject
    lateinit var connectToHostUseCase: ConnectToHostUseCase

    @Inject
    lateinit var sendPayloadUseCase: SendPayloadUseCase

    private val binder = LocalBinder()
    private var timerCmInfo: TimerCommunicateInfo? = null

    override fun onBind(intent: Intent): IBinder {
        super.onBind(intent)
        return binder
    }

    fun connectToHost(
        userEntity: UserEntity?,
        hostEndpointId: String,
        onUpdateUiState: (TimerCommunicateInfo?) -> Unit,
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

                            if (payloadType is PayloadType.UpdateTimerCmInfo) {
                                timerCmInfo = payloadType.timerCommunicateInfo
                                onUpdateUiState(timerCmInfo)
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
                        onOpenDialog(
                            DialogScreen.DialogWarning(
                                message = "호스트와 연결이 끊어졌습니다.\n" +
                                        "진행되고 있는 타이머는 중단됩니다.",
                                isError = true
                            )
                        )
                    }

                    is ClientEvent.ClientConnectionFailure -> {

                    }

                    else -> {}
                }
            }
        }

    inner class LocalBinder : Binder() {
        fun getService(): ClientTimerService = this@ClientTimerService
    }
}