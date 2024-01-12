package com.sghore.needtalk.presentation.ui.create_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.usecase.GetTimerSettingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val getTimerSettingUseCase: GetTimerSettingUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateUiState())
    private val _uiEvent = MutableSharedFlow<CreateUiEvent>()

    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        CreateUiState()
    )
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    init {
        initState()
    }

    private fun initState() = viewModelScope.launch {
        _uiState.value = getTimerSettingUseCase()
    }

    // 시간 변경 이벤트
    fun changeTalkTime(talkTime: Long) {
        _uiState.update {
            it.copy(talkTime = talkTime)
        }
    }

    // 스톱워치 모드 온 오프
    fun stopwatchOnOff(isAllow: Boolean) {
        _uiState.update {
            it.copy(isStopwatch = isAllow)
        }
    }

    // 음악 반복 온 오프
    fun repeatMusicOnOff(isAllow: Boolean) {
        _uiState.update {
            it.copy(allowRepeatMusic = isAllow)
        }
    }

    // 인원 수 변경
    fun changeNumberOfPeople(number: Int) {
        _uiState.update {
            it.copy(numberOfPeople = number)
        }
    }

    fun handelEvent(event: CreateUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}