package com.sghore.needtalk.presentation.ui.profile_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.Friend
import com.sghore.needtalk.domain.usecase.AddFriendUseCase
import com.sghore.needtalk.domain.usecase.GetAllFriendsUseCase
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
    private val getAllFriendsUseCase: GetAllFriendsUseCase,
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
            getAllFriendsUseCase().collectLatest { friends ->
                _uiState.update { it.copy(friends = friends) }
            }
        }
    }

    fun handelEvent(event: ProfileUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}