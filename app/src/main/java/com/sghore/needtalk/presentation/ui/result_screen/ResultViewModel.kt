package com.sghore.needtalk.presentation.ui.result_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.FriendEntity
import com.sghore.needtalk.data.repository.UserRepository
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

@OptIn(ExperimentalSerializationApi::class)
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

    private var talkTime: Long = 0L
    private var filePath: String? = null

    init {
        filePath = savedStateHandle.get<String>("filePath")
        talkTime = savedStateHandle.get<Long>("talkTime") ?: 0L
        val userTalkResultJsonArr =
            savedStateHandle.get<String>("userTalkResults") ?: ""

        if (talkTime != 0L && filePath != null && userTalkResultJsonArr.isNotEmpty()) {
            // 원본으로 변환
            val userTalkResults: List<UserTalkResult> = Json.decodeFromString(userTalkResultJsonArr)
            initUiState(filePath!!, userTalkResults)
        }
    }

    // UI State 초기화
    private fun initUiState(path: String, userTalkResults: List<UserTalkResult>) =
        viewModelScope.launch {
            val fileSize = if (path.isEmpty()) {
                ""
            } else {
                getFileSize(path)
            }
            val otherUsers = userTalkResults.map {
                getUserDataUseCase(it.userId)
            }

            _uiState.update {
                it.copy(
                    fileSize = fileSize,
                    otherUsers = otherUsers,
                    userTalkResult = userTalkResults,
                    isLoading = false
                )
            }
        }

    // 파일 사이즈를 가져옴
    private fun getFileSize(filePath: String): String {
        val size = File(filePath).length()
        val df = DecimalFormat("0.00")

        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb

        return if (size < sizeMb) df.format(size / sizeKb) + "KB"
        else if (size < sizeGb) df.format(size / sizeMb) + " MB"
        else ""
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

        _uiState.update {
            it.copy(otherUsers = updateOtherUsers)
        }
    }

    // 대화기록 저장
    fun saveTalkHistory() = viewModelScope.launch {
        val stateValue = _uiState.value
        saveTalkHistoryUseCase(
            talkTitle = stateValue.talkTitle,
            talkTime = talkTime,
            filePath = filePath ?: "",
            otherUsers = stateValue.otherUsers
        )
    }

    fun handelEvent(event: ResultUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}