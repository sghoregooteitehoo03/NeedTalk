package com.sghore.needtalk.presentation.ui.create_talk_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateTalkViewModel @Inject constructor(
    private val talkRepository: TalkRepository
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(CreateTalkUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = CreateTalkUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<CreateTalkUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    init {
        viewModelScope.launch {
            // 저장된 대화방 설정이 있는지 확인
            val talkSettingEntity = talkRepository.getTalkSettingEntity().first()
            if (talkSettingEntity != null) { // 저장된 데이터가 있다면
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        talkTime = talkSettingEntity.talkTime,
                        isTimer = talkSettingEntity.isTimer,
                        isMicAllow = talkSettingEntity.isAllowMic,
                        numberOfPeople = talkSettingEntity.numberOfPeople
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 타이머 정보 저장
    fun completeTimerSetting(
        navigateToTimer: (TimerCommunicateInfo) -> Unit
    ) = viewModelScope.launch {
        val stateValue = _uiState.value

//        if (stateValue.talkTime == 0L) {
//            handelEvent(CreateTalkUiEvent.ErrorMessage("0분 이상 대화 시간을 설정해주세요."))
//            return@launch
//        }
//
//        val timerSetting = TalkSettingEntity(
//            userId = stateValue.userEntity?.userId ?: "",
//            talkTime = stateValue.talkTime,
//            isTimer = stateValue.isStopwatch,
//            numberOfPeople = stateValue.numberOfPeople
//        )
//
//        insertTimerSettingUseCase(timerSetting)
//        navigateToTimer(
//            TimerCommunicateInfo(
//                participantInfoList = listOf(
//                    ParticipantInfo(
//                        stateValue.userEntity!!,
//                        endpointId = "",
//                        isReady = null
//                    )
//                ),
//                currentTime = if (timerSetting.isTimer) {
//                    0L
//                } else {
//                    timerSetting.talkTime
//                },
//                maxTime = if (timerSetting.isTimer) {
//                    -1L
//                } else {
//                    timerSetting.talkTime
//                },
//                isStopWatch = timerSetting.isTimer,
//                maxMember = timerSetting.numberOfPeople,
//                timerActionState = TimerActionState.TimerWaiting
//            )
//        )
    }

    // 스톱워치 모드 온 오프
    fun setTimerAllow(isAllow: Boolean) {
        _uiState.update { it.copy(isTimer = isAllow) }
    }

    // 인원 수 변경
    fun changeNumberOfPeople(number: Int) {
        _uiState.update {
            it.copy(numberOfPeople = number)
        }
    }

    // 이벤트 처리
    fun handelEvent(event: CreateTalkUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}