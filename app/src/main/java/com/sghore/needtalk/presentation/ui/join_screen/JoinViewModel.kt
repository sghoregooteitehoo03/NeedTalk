package com.sghore.needtalk.presentation.ui.join_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.ClientEvent
import com.sghore.needtalk.domain.model.TimerInfo
import com.sghore.needtalk.domain.usecase.ConnectToHostUseCase
import com.sghore.needtalk.domain.usecase.StartDiscoveryUseCase
import com.sghore.needtalk.domain.usecase.StopAllConnectionUseCase
import com.sghore.needtalk.domain.usecase.StopCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.nio.charset.Charset
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val startDiscoveryUseCase: StartDiscoveryUseCase,
    private val connectToHostUseCase: ConnectToHostUseCase,
    private val stopAllConnectionUseCase: StopAllConnectionUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(JoinUiState())
    private val _uiEvent = MutableSharedFlow<JoinUiEvent>()

    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        JoinUiState()
    )
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    init {
        val userEntityJson = savedStateHandle.get<String>("userEntity")
        val packageName = savedStateHandle.get<String>("packageName") ?: ""

        if (userEntityJson != null && packageName.isNotEmpty()) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)

            _uiState.update { it.copy(userEntity = userEntity) }
            startDiscovery(packageName)
        }
    }

    @OptIn(FlowPreview::class)
    private fun startDiscovery(packageName: String) = viewModelScope.launch {
        val endpointList = mutableListOf<String>()
        var isError = false

        startDiscoveryUseCase(packageName)
            .onEach { event ->
                when (event) {
                    is ClientEvent.DiscoveryEndpointFound -> {
                        // 근처 기기를 발견 하였을 경우
                        endpointList.add(event.endpointId)
                        Log.i("Check", "EndpointFound")
                    }

                    is ClientEvent.DiscoveryEndpointLost -> {

                    }

                    is ClientEvent.DiscoveryFailure -> {
                        isError = true
                    }

                    else -> {}
                }
            }
            .debounce(3000)
            .onEach { _ ->
                if (!isError) {
                    stopAllConnectionUseCase(StopCase.StopDiscovery) // 찾는 과정을 멈춤
                    _uiState.update {
                        it.copy(
                            searchNearDevice = SearchNearDevice.Searching(true)
                        )
                    }

                    Log.i("Check", "endpointList: $endpointList")
                    delay(1000)

                    _uiState.update {
                        it.copy(
                            searchNearDevice = SearchNearDevice.Load(
                                endpointIdList = endpointList,
                                timerInfoList = endpointList.map { null }
                            )
                        )
                    }
                }
            }
            .collect()
    }

    // 생성한 호스트가 만든 타이머의 정보를 가져옴
    fun loadTimerInfo(index: Int) = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoadInfo = true)
        }

        val previousLoadData =
            _uiState.value.searchNearDevice as SearchNearDevice.Load
        val endpointId = previousLoadData.endpointIdList[index]
        val userId = _uiState.value.userEntity?.userId ?: ""

        connectToHostUseCase(userId, endpointId)
            .collectLatest { event ->
                when (event) {
                    is ClientEvent.PayloadReceived -> {
                        val timerInfoJson =
                            event.payload.asBytes()?.toString(Charset.defaultCharset())

                        if (timerInfoJson != null) {
                            val timerInfo =
                                Json.decodeFromString(TimerInfo.serializer(), timerInfoJson)
                            val updateList = previousLoadData.timerInfoList
                                .toMutableList()
                                .apply {
                                    this[index] = timerInfo
                                }

                            stopAllConnectionUseCase(StopCase.StopConnections) // 연결을 없앰
                            _uiState.update {
                                it.copy(
                                    isLoadInfo = false,
                                    searchNearDevice = SearchNearDevice.Load(
                                        endpointIdList = previousLoadData.endpointIdList,
                                        timerInfoList = updateList
                                    )
                                )
                            }
                        }
                    }

                    is ClientEvent.ClientConnectionFailure -> {
                        _uiState.update {
                            it.copy(isLoadInfo = false)
                        }
                        handelEvent(JoinUiEvent.ShowSnackBar(event.errorMessage))
                    }

                    else -> {}
                }
            }
    }

    fun handelEvent(event: JoinUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}