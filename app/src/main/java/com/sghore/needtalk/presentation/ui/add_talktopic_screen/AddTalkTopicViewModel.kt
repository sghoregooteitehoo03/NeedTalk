package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class AddTalkTopicViewModel @Inject constructor() : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(AddTalkTopicUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddTalkTopicUiState()
    )
}