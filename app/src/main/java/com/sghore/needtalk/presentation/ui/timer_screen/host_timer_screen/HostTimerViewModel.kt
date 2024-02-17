package com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen

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
class HostTimerViewModel @Inject constructor(
    private val insertTalkEntityUseCase: InsertTalkEntityUseCase,
    private val insertUserEntityUseCase: InsertUserEntityUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
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
        val userEntityJson = savedStateHandle.get<String>("userEntity")
        val timerCmInfoJson = savedStateHandle.get<String>("timerCmInfo")

        if (userEntityJson != null && timerCmInfoJson != null) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)
            val timerCmInfo =
                Json.decodeFromString(TimerCommunicateInfo.serializer(), timerCmInfoJson)

            _uiState.update {
                it.copy(
                    userEntity = userEntity,
                    timerCommunicateInfo = timerCmInfo,
                    talkTopic = timerCmInfo.talkTopics.random().topic
                )
            }
        }
    }

    fun updateTimerCommunicateInfo(timerCommunicateInfo: TimerCommunicateInfo) {
        _uiState.update {
            it.copy(timerCommunicateInfo = timerCommunicateInfo)
        }
    }

    fun changeTalkTopic() {
        val talkTopics = _uiState.value.timerCommunicateInfo.talkTopics
        _uiState.update { it.copy(talkTopic = talkTopics.random().topic) }
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

    // 참여한 인원들에 대한 정보를 저장함
    fun saveOtherUserData() = viewModelScope.launch {
        val timerCmInfo = _uiState.value.timerCommunicateInfo
        userList = timerCmInfo.participantInfoList.map { it?.userEntity }

        for (i in 1 until timerCmInfo.participantInfoList.size) {
            insertUserEntityUseCase(timerCmInfo.participantInfoList[i]!!.userEntity)
        }
    }

    // 타이머 끝났을 떄 취하는 동작
    fun saveTalkHistory(showToastBar: (String) -> Unit) = viewModelScope.launch {
        val updateTimerInfo = _uiState.value.timerCommunicateInfo

        if (updateTimerInfo.timerActionState != TimerActionState.TimerWaiting) {
            val talkTime = if (updateTimerInfo.isStopWatch)
                updateTimerInfo.currentTime
            else
                updateTimerInfo.maxTime - updateTimerInfo.currentTime

            if (talkTime >= 60000) {
                val talkHistory = TalkHistory(
                    talkTime = talkTime,
                    users = userList,
                    createTimeStamp = System.currentTimeMillis()
                )

                // 대화 정보를 저장함
                insertTalkEntityUseCase(talkHistory)
            } else {
                showToastBar("1분 이하의 대화는 기록되지 않습니다.")
            }
        }
    }
}