package com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.ConnectionEvent
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.domain.usecase.StartAdvertisingUseCase
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
class HostTimerViewModel @Inject constructor(
    private val startAdvertisingUseCase: StartAdvertisingUseCase,
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
        val timerCmInfoJson = savedStateHandle.get<String>("timerCmInfo")
        val packageName = savedStateHandle.get<String>("packageName") ?: ""

        if (userEntityJson != null && timerCmInfoJson != null && packageName.isNotEmpty()) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)
            val timerCmInfo =
                Json.decodeFromString(TimerCommunicateInfo.serializer(), timerCmInfoJson)

            _uiState.update {
                it.copy(
                    userEntity = userEntity,
                    timerCommunicateInfo = timerCmInfo
                )
            }
            startAdvertising(
                userEntity = userEntity,
                packageName = packageName
            )
        }
    }

    private fun startAdvertising(
        userEntity: UserEntity,
        packageName: String
    ) = viewModelScope.launch {
        startAdvertisingUseCase(
            userId = userEntity.userId,
            packageName = packageName
        )
            .collectLatest { event ->
                when (event) {
                    // 기기간의 연결이 문제가 없는경우
                    is ConnectionEvent.SuccessConnect -> {
                        val timerCmInfo = _uiState.value.timerCommunicateInfo
                        sendUpdateTimerCmInfo(
                            updateTimerCmInfo = timerCmInfo,
                            endpointId = event.endpointId,
                            onFailure = {

                            }
                        )
                    }

                    // 어떤 기기와 연결이 끊어진 경우
                    is ConnectionEvent.Disconnected -> {
                        val participantInfoList = _uiState.value
                            .timerCommunicateInfo
                            ?.participantInfoList
                            ?.toMutableList()

                        val disconnectUser = participantInfoList?.filter {
                            it.endpointId == event.endpointId
                        }

                        if ((disconnectUser?.isNotEmpty()) == true) {
                            // 참가자의 정보를 지움
                            participantInfoList.remove(disconnectUser[0])
                            val updateTimerCmInfo = _uiState.value
                                .timerCommunicateInfo
                                ?.copy(participantInfoList = participantInfoList)

                            _uiState.update {
                                it.copy(timerCommunicateInfo = updateTimerCmInfo)
                            }

                            // 지워진 정보를 업데이트 한 후 다른 기기들 갱신
                            for (i in 1 until participantInfoList.size) {
                                sendUpdateTimerCmInfo(
                                    updateTimerCmInfo,
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
                            val payloadType = Json.decodeFromString(
                                PayloadType.serializer(),
                                payloadTypeJson
                            )

                            when (payloadType) {
                                // 다른 유저가 생성한 타이머에 참가하였을 때
                                is PayloadType.ClientJoinTimer -> {
                                    val participantInfoList = _uiState.value
                                        .timerCommunicateInfo
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
                                    val updateTimerCmInfo =
                                        _uiState.value
                                            .timerCommunicateInfo
                                            ?.copy(participantInfoList = participantInfoList)

                                    _uiState.update {
                                        it.copy(timerCommunicateInfo = updateTimerCmInfo)
                                    }

                                    // 업데이트 된 데이터를 참가자들에게 전달
                                    for (i in 1 until participantInfoList.size) {
                                        sendUpdateTimerCmInfo(
                                            updateTimerCmInfo,
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

    fun handelEvent(event: TimerUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update {
            it.copy(dialogScreen = dialogScreen)
        }
    }

    // 타이머 동작
    fun runTimer() {
        val updateTimerCmInfo =
            _uiState.value
                .timerCommunicateInfo?.copy(timerActionState = TimerActionState.TimerRunning)

        _uiState.update {
            it.copy(timerCommunicateInfo = updateTimerCmInfo)
        }
        for (i in 1 until (updateTimerCmInfo?.participantInfoList?.size ?: 1)) {
            sendUpdateTimerCmInfo(
                updateTimerCmInfo = updateTimerCmInfo,
                endpointId = updateTimerCmInfo?.participantInfoList?.get(i)?.endpointId ?: "",
                onFailure = {}
            )
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
}