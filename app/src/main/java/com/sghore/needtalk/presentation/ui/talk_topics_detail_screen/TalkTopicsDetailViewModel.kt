package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

class TalkTopicsDetailViewModel @Inject constructor() : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(TalkTopicsDetailUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TalkTopicsDetailUiState()
    )


}