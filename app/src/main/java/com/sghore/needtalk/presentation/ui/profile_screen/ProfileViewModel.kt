package com.sghore.needtalk.presentation.ui.profile_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.usecase.AddFriendUseCase
import com.sghore.needtalk.domain.usecase.GetAllUserDataUseCase
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
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val getAllFriendUseCase: GetAllUserDataUseCase,
    private val addFriendUseCase: AddFriendUseCase
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ProfileUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<ProfileUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly
    )

    init {
        // TODO: 나중에 테스트
        viewModelScope.launch {
            // 친구 목록을 가져옴
            getAllFriendUseCase().collectLatest { friends ->
                _uiState.update {
                    it.copy(
                        friends = friends,
                        isLoading = false
                    )
                }
            }
        }
    }

    // 다이얼로그 화면 설정
    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update {
            it.copy(dialogScreen = dialogScreen)
        }
    }

    // 친구 삭제
    fun removeFriend(friendId: String) {
        // TODO: 나중에 테스트
        viewModelScope.launch {
            userRepository.deleteFriendEntity(friendId)
        }
    }

    fun handelEvent(event: ProfileUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}