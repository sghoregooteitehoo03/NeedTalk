package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.usecase.GetPopularTalkTopicsUseCase
import com.sghore.needtalk.domain.usecase.GetTalkTopicGroupUseCase
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
class TalkTopicsViewModel @Inject constructor(
    val getPopularTalkTopicsUseCase: GetPopularTalkTopicsUseCase,
    val getTalkTopicGroupUseCase: GetTalkTopicGroupUseCase
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(TalkTopicsUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TalkTopicsUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<TalkTopicsUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly
    )

    init {
        getTalkTopicGroups()
        getPopularTopics()
    }

    private fun getTalkTopicGroups() = viewModelScope.launch {
        getTalkTopicGroupUseCase().collectLatest { groups ->
            _uiState.update { it.copy(talkTopicGroups = groups) }
        }
    }

    // 인기 대화주제 5개 가져오기
    private fun getPopularTopics() = viewModelScope.launch {
        //val talkTopics = getPopularTalkTopicsUseCase()
        _uiState.update { it.copy(isLoading = false, popularTalkTopics = listOf()) }
    }

    fun handelEvent(event: TalkTopicsUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}