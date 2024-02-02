package com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.usecase.InsertTalkEntityUseCase
import com.sghore.needtalk.domain.usecase.InsertUserEntityUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiEvent
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class ClientTimerViewModel @Inject constructor(
    private val insertTalkEntityUseCase: InsertTalkEntityUseCase,
    private val insertUserEntityUseCase: InsertUserEntityUseCase,
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

        if (userEntityJson != null && hostEndpointId != null) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)

            _uiState.update {
                it.copy(
                    userEntity = userEntity,
                    hostEndpointId = hostEndpointId
                )
            }
        }
    }

    fun updateTimerCommunicateInfo(timerCommunicateInfo: TimerCommunicateInfo?) {
        when (timerCommunicateInfo?.timerActionState) {
            is TimerActionState.TimerReady -> {
                if (_uiState.value.timerCommunicateInfo?.timerActionState == TimerActionState.TimerWaiting)
                    saveOtherUserData()
            }

            is TimerActionState.TimerFinished -> {
                saveTalkHistory(timerCommunicateInfo)
            }

            else -> {}
        }

        _uiState.update {
            it.copy(timerCommunicateInfo = timerCommunicateInfo)
        }
    }

    fun handelEvent(event: TimerUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    fun setDialogScreen(dialogScreen: DialogScreen) {
        if (dialogScreen != _uiState.value.dialogScreen) {
            _uiState.update {
                it.copy(dialogScreen = dialogScreen)
            }
        }
    }

    private fun saveOtherUserData() = viewModelScope.launch {
        val timerCmInfo = _uiState.value.timerCommunicateInfo
        for (i in 1 until (timerCmInfo?.participantInfoList?.size ?: 0)) {
            insertUserEntityUseCase(timerCmInfo?.participantInfoList?.get(i)!!.userEntity)
        }
    }

    // 타이머 끝났을 떄 취하는 동작
    private fun saveTalkHistory(updateTimerInfo: TimerCommunicateInfo?) = viewModelScope.launch {
        val talkHistory = TalkHistory(
            talkTime = updateTimerInfo?.maxTime ?: 0L,
            users = updateTimerInfo?.participantInfoList?.map { it?.userEntity } ?: listOf(),
            createTimeStamp = System.currentTimeMillis()
        )

        // 대화 정보를 저장함
        insertTalkEntityUseCase(talkHistory)
    }
}