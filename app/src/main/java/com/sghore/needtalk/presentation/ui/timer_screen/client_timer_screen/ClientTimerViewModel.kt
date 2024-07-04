package com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen

import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.domain.model.UserTalkResult
import com.sghore.needtalk.domain.usecase.InsertUserEntity2UseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiEvent
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiState
import com.sghore.needtalk.util.byteArrayToBitmap
import com.sghore.needtalk.util.getRandomExperiencePoint
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
class ClientTimerViewModel @Inject constructor(
    private val insertUserEntityUseCase: InsertUserEntity2UseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(TimerUiState())
    private val _uiEvent = MutableSharedFlow<TimerUiEvent>()
    private var userTalkResults = mutableListOf<UserTalkResult>()// 각 유저 별 대화시간 저장

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

    fun updateTimerCommunicateInfo(
        timerCommunicateInfo: TimerCommunicateInfo,
        onTimerStart: ((String) -> Unit) -> Unit
    ) {
        when (timerCommunicateInfo.timerActionState) {
            is TimerActionState.TimerReady -> {
                if (_uiState.value.timerCommunicateInfo.timerActionState == TimerActionState.TimerWaiting) {
                    onTimerStart(::saveOtherUserData) // 타이머가 시작되면 다른 유저 정보 저장
                }
            }

            else -> {}
        }

        _uiState.update {
            it.copy(timerCommunicateInfo = timerCommunicateInfo)
        }

        // 참가자가 중간에 나갔는지 확인
        checkExitParticipant(
            participantInfoList = timerCommunicateInfo.participantInfoList,
            talkTime = if (timerCommunicateInfo.isTimer) {
                timerCommunicateInfo.maxTime - timerCommunicateInfo.currentTime
            } else {
                timerCommunicateInfo.currentTime
            }
        )
    }

    // 참가자가 중간에 나갔는지 확인
    private fun checkExitParticipant(
        participantInfoList: List<ParticipantInfo?>,
        talkTime: Long
    ) {
        // 참가자가 중간에 나갔을 경우
        if (participantInfoList.filterNotNull().size - 1 != userTalkResults.size && userTalkResults.isNotEmpty()) {
            for (i in userTalkResults.indices) {
                var isFound = false

                // 나간 유저가 누군지 확인
                for (j in participantInfoList.indices) {
                    if (participantInfoList[j]?.userId == userTalkResults[i].userId) {
                        isFound = true
                        break
                    }
                }

                // 나간 유저를 찾은 경우
                if (!isFound) {
                    // 나간 유저가 집중한 대화시간을 저장함
                    userTalkResults[i] = userTalkResults[i].copy(
                        talkTime = talkTime,
                        experiencePoint = getRandomExperiencePoint(talkTime)
                    )
                    break
                }
            }
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

    // 참여한 다른 참가자들에 대한 정보를 저장함
    private fun saveOtherUserData(currentUserId: String) {
        viewModelScope.launch {
            val timerCmInfo = _uiState.value.timerCommunicateInfo
            val participantInfoList = timerCmInfo.participantInfoList
                .filter { it?.userId != currentUserId } // 사용자의 정보만 제외
            userTalkResults = participantInfoList
                .map {
                    UserTalkResult(userId = it?.userId ?: "", talkTime = 0, experiencePoint = 0.0)
                }.toMutableList()

            // 참가자들에 정보를 모두 저장함
            for (participantInfo in participantInfoList) {
                if (participantInfo != null) {
                    val userData = UserData(
                        userId = participantInfo.userId,
                        name = participantInfo.name,
                        profileImage = byteArrayToBitmap(participantInfo.profileImage).asImageBitmap(),
                        experiencePoint = 0f,
                        friendshipPoint = -1
                    )

                    insertUserEntityUseCase(
                        userData = userData,
                        selectedHairImageRes = -1,
                        selectedFaceImageRes = -1,
                        selectedAccessoryImageRes = -1
                    )
                }
            }
        }
    }

    // 타이머 끝났을 떄 취하는 동작
    fun finishedTalk(
        currentUserId: String,
        recordFilePath: String,
        navigateOtherScreen: (Boolean, List<UserTalkResult>, String) -> Unit
    ) {
        val timerCmInfo = _uiState.value.timerCommunicateInfo
        val currentTime = if (timerCmInfo.isTimer) {
            timerCmInfo.maxTime - timerCmInfo.currentTime
        } else {
            timerCmInfo.currentTime
        }
        val isFinished = currentTime >= 300000

        if (isFinished) { // 대화가 조건에 만족하여 끝났을 경우
            val experiencePoint = getRandomExperiencePoint(currentTime)

            timerCmInfo.participantInfoList
                .filterNotNull()
                .filter { it.userId != currentUserId }
                .forEach { participantInfo ->
                    userTalkResults.forEachIndexed { i, userTalkResult ->
                        if (userTalkResult.userId == participantInfo.userId) {
                            userTalkResults[i] = userTalkResult.copy(
                                talkTime = currentTime,
                                experiencePoint = experiencePoint
                            )
                        }
                    }
                }
            navigateOtherScreen(true, userTalkResults, recordFilePath)
        } else {
            removeTempRecordFile(recordFilePath)
            navigateOtherScreen(false, listOf(), "")
        }
    }

    // 임시 레코드 파일을 지움
    private fun removeTempRecordFile(recordFilePath: String) {
        if (_uiState.value.timerCommunicateInfo.isAllowMic) {
            val recordFile = File(recordFilePath)

            if (recordFile.exists()) {
                recordFile.delete()
            }
        }
    }
}