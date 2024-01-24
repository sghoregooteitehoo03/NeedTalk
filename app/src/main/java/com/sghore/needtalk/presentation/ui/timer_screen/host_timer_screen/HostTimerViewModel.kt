package com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.nearby.connection.Payload
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.ConnectionEvent
import com.sghore.needtalk.domain.model.TimerInfo
import com.sghore.needtalk.domain.usecase.SendPayloadUseCase
import com.sghore.needtalk.domain.usecase.StartAdvertisingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class HostTimerViewModel @Inject constructor(
    private val startAdvertisingUseCase: StartAdvertisingUseCase,
    private val sendPayloadUseCase: SendPayloadUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var userEntity by mutableStateOf<UserEntity?>(null)
    var timerInfo by mutableStateOf<TimerInfo?>(null)

    init {
        val userEntityJson = savedStateHandle.get<String>("userEntity")
        val timerInfoJson = savedStateHandle.get<String>("timerInfo")
        val packageName = savedStateHandle.get<String>("packageName") ?: ""

        if (userEntityJson != null && timerInfoJson != null && packageName.isNotEmpty()) {
            userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)
            timerInfo =
                Json.decodeFromString(TimerInfo.serializer(), timerInfoJson)

            startAdvertising(
                userId = userEntity?.userId ?: "",
                packageName = packageName,
                sendData = timerInfoJson
            )
        }
    }

    private fun startAdvertising(
        userId: String,
        packageName: String,
        sendData: String
    ) = viewModelScope.launch {
        startAdvertisingUseCase(userId, packageName)
            .collectLatest { event ->
                when (event) {
                    // 기기간의 연결이 문제가 없는경우
                    is ConnectionEvent.ConnectionResultSuccess -> {
                        val payload = Payload.fromBytes(sendData.toByteArray())

                        // 다른 기기에게 타이머에 대한 정보를 전달함
                        sendPayloadUseCase(
                            payload = payload,
                            endpointId = event.endpointId,
                            onFailure = {

                            })
                    }

                    else -> {}
                }
            }
    }
}