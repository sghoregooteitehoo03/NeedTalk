package com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.ClientEvent
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.domain.usecase.ConnectToHostUseCase
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiEvent
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import javax.inject.Inject

@HiltViewModel
class ClientTimerViewModel @Inject constructor(
    private val connectToHostUseCase: ConnectToHostUseCase,
    private val sendPayloadUseCase: SendPayloadUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimerUiState())
    private val _uiEvent = MutableSharedFlow<TimerUiEvent>()

    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        TimerUiState()
    )
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    init {
        val userEntityJson = savedStateHandle.get<String>("userEntity")
        val hostEndpointId = savedStateHandle.get<String>("hostEndpointId")
        val packageName = savedStateHandle.get<String>("packageName") ?: ""

        if (userEntityJson != null && hostEndpointId != null && packageName.isNotEmpty()) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)

            _uiState.update {
                it.copy(userEntity = userEntity)
            }
            connectToHost(
                userEntity = userEntity,
                hostEndpointId = hostEndpointId
            )
        }
    }

    fun handelEvent(event: TimerUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update {
            it.copy(dialogScreen = dialogScreen)
        }
    }

    private fun connectToHost(userEntity: UserEntity, hostEndpointId: String) =
        viewModelScope.launch {
            connectToHostUseCase(
                userId = userEntity.userId,
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
                                _uiState.update {
                                    it.copy(timerCommunicateInfo = payloadType.timerCommunicateInfo)
                                }
                            }
                        }
                    }

                    // 연결이 성공적으로 됨
                    is ClientEvent.SuccessConnect -> {
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

                    // host와 연결이 끊어졌을 때
                    is ClientEvent.Disconnect -> {
                        _uiState.update {
                            it.copy(
                                dialogScreen = DialogScreen.DialogWarning(
                                    message = "호스트와 연결이 끊어졌습니다.\n" +
                                            "진행되고 있는 타이머는 중단됩니다.",
                                    isError = true
                                ),
                            )
                        }
                    }

                    is ClientEvent.ClientConnectionFailure -> {

                    }

                    else -> {}
                }
            }
        }
}