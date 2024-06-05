package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.usecase.GetPopularTalkTopicsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TalkTopicsViewModel @Inject constructor(
    val repository: TalkTopicRepository,
    val getPopularTalkTopicsUseCase: GetPopularTalkTopicsUseCase
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(TalkTopicsUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TalkTopicsUiState()
    )

    init {
        getPopularTopics()
    }

    // 인기 대화주제 5개 가져오기
    private fun getPopularTopics() = viewModelScope.launch {
        val talkTopics = getPopularTalkTopicsUseCase()
        _uiState.update {
            it.copy(popularTalkTopics = talkTopics)
        }
    }

    // TODO: 나중에 지울 것
    fun setData() = viewModelScope.launch {
        repository.setData()
    }
}