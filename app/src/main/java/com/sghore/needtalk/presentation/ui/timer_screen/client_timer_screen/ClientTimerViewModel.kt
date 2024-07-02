package com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
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
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ClientTimerViewModel @Inject constructor(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val _uiState = MutableStateFlow(TimerUiState())
    private val _uiEvent = MutableSharedFlow<TimerUiEvent>()
    private var userList = listOf<UserEntity?>()

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
        val hostEndpointId = savedStateHandle.get<String>("hostEndpointId")

        if (hostEndpointId != null) {
            _uiState.update { it.copy(hostEndpointId = hostEndpointId) }
        }
    }

    fun updateTimerCommunicateInfo(timerCommunicateInfo: TimerCommunicateInfo) {
        when (timerCommunicateInfo.timerActionState) {
            is TimerActionState.TimerReady -> {
                if (_uiState.value.timerCommunicateInfo.timerActionState == TimerActionState.TimerWaiting) {
                    saveOtherUserData()
                }
            }

            else -> {}
        }

        _uiState.update {
            it.copy(timerCommunicateInfo = timerCommunicateInfo)
        }
    }

    fun updateAmplitudeValue(amplitude: Int) {
        _uiState.update {
            it.copy(amplitudeValue = amplitude)
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

    fun flipState(isFlip: Boolean) {
        _uiState.update { it.copy(isFlip = isFlip) }
    }

    private fun saveOtherUserData() = viewModelScope.launch {
//        val timerCmInfo = _uiState.value.timerCommunicateInfo
//        userList = timerCmInfo.participantInfoList.map { it?.userData }
//
//        for (i in 0 until timerCmInfo.participantInfoList.size) {
//            insertUserEntityUseCase(timerCmInfo.participantInfoList[i]!!.userData)
//        }
    }

    // 타이머 끝났을 떄 취하는 동작
    fun finishedTalk(
        recordFilePath: String,
        navigateOtherScreen: (Boolean) -> Unit
    ) {
        val timerCmInfo = _uiState.value.timerCommunicateInfo
        if (timerCmInfo.timerActionState != TimerActionState.TimerWaiting) {
            val currentTime = if (timerCmInfo.isTimer) {
                timerCmInfo.maxTime - timerCmInfo.currentTime
            } else {
                timerCmInfo.currentTime
            }
            val isFinished = currentTime >= 300000

            if (isFinished) {
                navigateOtherScreen(true)
            } else {
                removeTempRecordFile(recordFilePath)
                navigateOtherScreen(false)
            }
        }
    }

    // 임시 레코드 파일을 지움
    private fun removeTempRecordFile(recordFilePath: String) {
        val recordFile = File(recordFilePath)

        if (recordFile.exists()) {
            recordFile.delete()
        }
    }
}