package com.sghore.needtalk.presentation.ui.groups_detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.domain.usecase.DeleteTalkTopicGroupUseCase
import com.sghore.needtalk.domain.usecase.GetAllTalkTopicGroupUseCase
import com.sghore.needtalk.domain.usecase.InsertTalkTopicGroupUseCase
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
class GroupsDetailViewModel @Inject constructor(
    private val getAllTalkTopicGroupUseCase: GetAllTalkTopicGroupUseCase,
    private val insertTalkTopicGroupUseCase: InsertTalkTopicGroupUseCase,
    private val deleteTalkTopicGroupUseCase: DeleteTalkTopicGroupUseCase
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

    // 다이얼로그 화면 설정
    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update { it.copy(dialogScreen = dialogScreen) }
    }

    // 모음집 이름 수정
    fun editTalkTopicGroup(group: TalkTopicGroup) = viewModelScope.launch {
        insertTalkTopicGroupUseCase(group)
    }

    // 모음집 삭제
    fun removeTalkTopicGroup(group: TalkTopicGroup) = viewModelScope.launch {
        deleteTalkTopicGroupUseCase(group)
    }

    // 이벤트 처리
    fun handleEvent(event: GroupsDetailUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}