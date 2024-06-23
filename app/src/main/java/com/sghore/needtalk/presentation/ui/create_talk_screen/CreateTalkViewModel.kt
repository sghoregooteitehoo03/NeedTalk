package com.sghore.needtalk.presentation.ui.create_talk_screen

import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.TalkSettingEntity
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.bitmapToByteArray
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
    fun completeTalkSetting(
        userData: UserData,
        selectedTime: Long,
        navigateToTimer: (TimerCommunicateInfo) -> Unit
    ) = viewModelScope.launch {
        val stateValue = _uiState.value
        val talkSetting = TalkSettingEntity(
            userId = userData.userId,
            talkTime = selectedTime,
            isTimer = stateValue.isTimer,
            isAllowMic = stateValue.isMicAllow,
            numberOfPeople = stateValue.numberOfPeople
        )

        // 정보 저장
        talkRepository.insertTalkSettingEntity(talkSetting)

        // 타이머 화면으로 이동
        navigateToTimer(
            // 대화방 타이머의 설정 정보를 생성
            TimerCommunicateInfo(
                participantInfoList = listOf(
                    ParticipantInfo(
                        userId = userData.userId,
                        name = userData.name,
                        profileImage = bitmapToByteArray(userData.profileImage.asAndroidBitmap()),
                        experiencePoint = userData.experiencePoint,
                        friendshipPoint = userData.friendshipPoint,
                        endpointId = "",
                        isReady = null
                    )
                ),
                currentTime = if (talkSetting.isTimer) {
                    0L
                } else {
                    talkSetting.talkTime
                },
                maxTime = if (talkSetting.isTimer) {
                    -1L
                } else {
                    talkSetting.talkTime
                },
                isStopWatch = talkSetting.isTimer,
                isAllowMic = talkSetting.isAllowMic,
                maxMember = talkSetting.numberOfPeople,
                timerActionState = TimerActionState.TimerWaiting
            )
        )
    }

    // 스톱워치 모드 온 오프
    fun setTimerAllow(isAllow: Boolean) {
        _uiState.update { it.copy(isTimer = isAllow) }
    }

    // 마이크 허용/비허용
    fun setMicAllow(isAllow: Boolean) {
        _uiState.update { it.copy(isMicAllow = isAllow) }
    }

    // 인원 수 변경
    fun changeNumberOfPeople(isIncrease: Boolean) {
        _uiState.update {
            it.copy(
                numberOfPeople = if (isIncrease) {
                    it.numberOfPeople + 1
                } else {
                    it.numberOfPeople - 1
                }
            )
        }
    }

    // 이벤트 처리
    fun handelEvent(event: CreateTalkUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}