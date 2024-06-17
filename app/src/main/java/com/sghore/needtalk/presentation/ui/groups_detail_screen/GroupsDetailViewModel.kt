package com.sghore.needtalk.presentation.ui.groups_detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.usecase.GetAllTalkTopicGroupUseCase
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
class GroupsDetailViewModel @Inject constructor(
    private val getAllTalkTopicGroupUseCase: GetAllTalkTopicGroupUseCase
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(GroupsDetailUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = GroupsDetailUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<GroupsDetailUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly
    )

    init {
        viewModelScope.launch {
            getAllTalkTopicGroupUseCase("")
                .collectLatest { groups ->
                    _uiState.update { it.copy(groups = groups) }
                }
        }
    }

    fun handleEvent(event: GroupsDetailUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}