package com.sghore.needtalk.presentation.ui.create_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.usecase.GetTalkTopicsUseCase
import com.sghore.needtalk.domain.usecase.GetTimerSettingUseCase
import com.sghore.needtalk.domain.usecase.InsertTimerSettingUseCase
import com.sghore.needtalk.domain.usecase.InsertTalkTopicUseCase
import com.sghore.needtalk.domain.usecase.RemoveTalkTopicUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
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
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val getTimerSettingUseCase: GetTimerSettingUseCase,
    private val getTalkTopicsUseCase: GetTalkTopicsUseCase,
    private val insertTalkTopicUseCase: InsertTalkTopicUseCase,
    private val removeTalkTopicUseCase: RemoveTalkTopicUseCase,
    private val insertTimerSettingUseCase: InsertTimerSettingUseCase,
    savedStateHandle: SavedStateHandle
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
        val userEntityJson = savedStateHandle.get<String>("userEntity")
        if (userEntityJson != null) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)
            initState(userEntity)
        } else {
            handelEvent(CreateUiEvent.ErrorMessage("오류가 발생하였습니다."))
        }
    }

    private fun initState(userEntity: UserEntity) = viewModelScope.launch {
        getTimerSettingUseCase().collectLatest { timerSettingEntity ->
            if (timerSettingEntity != null) { // 저장된 데이터가 있다면
                _uiState.update {
                    it.copy(
                        userEntity = userEntity,
                        isLoading = false,
                        talkTime = timerSettingEntity.talkTime,
                        isStopwatch = timerSettingEntity.isStopwatch,
                        numberOfPeople = timerSettingEntity.numberOfPeople
                    )
                }
            } else {
                _uiState.update {
                    it.copy(userEntity = userEntity, isLoading = false)
                }
            }
        }
    }

    // 대화주제 페이징하여 가져오기
    fun getTalkTopics(
        groupCode: Int,
        updateTopics: (List<TalkTopicEntity>) -> Unit
    ) = viewModelScope.launch {
        getTalkTopicsUseCase(groupCode).collectLatest {
            updateTopics(it)
        }
    }

    // 타이머 정보 저장
    fun completeTimerSetting(
        navigateToTimer: (TimerCommunicateInfo) -> Unit
    ) = viewModelScope.launch {
        val stateValue = _uiState.value

        if (stateValue.talkTime == 0L) {
            handelEvent(CreateUiEvent.ErrorMessage("0분 이상 대화 시간을 설정해주세요."))
            return@launch
        }

        val timerSetting = TimerSettingEntity(
            userId = stateValue.userEntity?.userId ?: "",
            talkTime = stateValue.talkTime,
            isStopwatch = stateValue.isStopwatch,
            numberOfPeople = stateValue.numberOfPeople
        )

        insertTimerSettingUseCase(timerSetting)
        navigateToTimer(
            TimerCommunicateInfo(
                participantInfoList = listOf(
                    ParticipantInfo(
                        stateValue.userEntity!!,
                        endpointId = "",
                        isReady = null
                    )
                ),
                currentTime = if (timerSetting.isStopwatch) {
                    0L
                } else {
                    timerSetting.talkTime
                },
                maxTime = if (timerSetting.isStopwatch) {
                    -1L
                } else {
                    timerSetting.talkTime
                },
                isStopWatch = timerSetting.isStopwatch,
                maxMember = timerSetting.numberOfPeople,
                talkTopics = listOf(),
                timerActionState = TimerActionState.TimerWaiting
            )
        )
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

    // 인원 수 변경
    fun changeNumberOfPeople(number: Int) {
        _uiState.update {
            it.copy(numberOfPeople = number)
        }
    }

    // 보여줄 다이얼로그 화면을 설정
    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update {
            it.copy(dialogScreen = dialogScreen)
        }
    }

    fun insertOrRemoveTalkTopic(
        talkTopicEntity: TalkTopicEntity,
        isRemove: Boolean
    ) = viewModelScope.launch {
        if (isRemove)
            removeTalkTopicUseCase(talkTopicEntity)
        else
            insertTalkTopicUseCase(talkTopicEntity)
    }

    fun handelEvent(event: CreateUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}