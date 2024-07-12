package com.sghore.needtalk.presentation.ui.result_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.FriendEntity
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.TalkResult
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.domain.model.UserTalkResult
import com.sghore.needtalk.domain.usecase.GetUserDataUseCase
import com.sghore.needtalk.domain.usecase.SaveTalkHistoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.DecimalFormat
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getUserDataUseCase: GetUserDataUseCase,
    private val saveTalkHistoryUseCase: SaveTalkHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ResultUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<ResultUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly
    )

    init {
        val talkResultJson =
            savedStateHandle.get<String>("talkResult") ?: ""

        if (talkResultJson.isNotEmpty()) {
            // 원본으로 변환
            val talkResult = Json.decodeFromString(TalkResult.serializer(), talkResultJson)
            initUiState(talkResult)
        }
    }

    // UI State 초기화
    private fun initUiState(talkResult: TalkResult) =
        viewModelScope.launch {
            val fileSize = if (talkResult.recordFilePath.isEmpty()) {
                0L
            } else {
                File(talkResult.recordFilePath).length()
            }
            val otherUsers = talkResult.userTalkResult.map {
                getUserDataUseCase(it.userId)
            }

            _uiState.update {
                it.copy(
                    fileSize = fileSize,
                    otherUsers = otherUsers,
                    talkResult = talkResult,
                    isLoading = false
                )
            }
        }

    // 타이틀 변경 이벤트 처리
    fun changeTalkTitle(title: String) {
        _uiState.update { it.copy(talkTitle = title) }
    }

    // 친구 추가
    fun addFriend(userId: String, index: Int) = viewModelScope.launch {
        val friendEntity = FriendEntity(
            userId = userId,
            experiencePoint = 0f,
            friendshipPoint = 0
        )
        userRepository.insertFriendEntity(friendEntity) // 친구 데이터 추가
        val friend = getUserDataUseCase(userId) // 유저 정보를 다시 가져옴

        // 유저 목록을 업데이트 함
        val updateOtherUsers = _uiState.value.otherUsers.toMutableList()
        updateOtherUsers[index] = friend

        _uiState.update { it.copy(otherUsers = updateOtherUsers) }
    }

    // 대화기록 저장
    fun saveTalkHistory() = viewModelScope.launch {
        val stateValue = _uiState.value
        val talkResult = stateValue.talkResult

        saveTalkHistoryUseCase(
            talkTitle = stateValue.talkTitle,
            talkTime = talkResult?.talkTime ?: 0L,
            filePath = talkResult?.recordFilePath ?: "",
            recordAmplitude = talkResult?.recordAmplitude ?: emptyList(),
            otherUsers = stateValue.otherUsers
        )
    }

    fun handelEvent(event: ResultUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}