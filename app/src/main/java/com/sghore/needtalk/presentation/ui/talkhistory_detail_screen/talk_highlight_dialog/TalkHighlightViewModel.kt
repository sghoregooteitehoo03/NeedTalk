package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen.talk_highlight_dialog

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.usecase.GetTalkHighlightUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TalkHighlightViewModel @Inject constructor(
    private val getTalkHighlightUseCase: GetTalkHighlightUseCase
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(TalkHighlightUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TalkHighlightUiState()
    )

    // MediaPlayer
    private var mediaPlayer: MediaPlayer? = null


    // initState
    fun initState(talkHistoryId: String) = viewModelScope.launch {
        if (_uiState.value.highlights == null) {
            getTalkHighlightUseCase(talkHistoryId)
                .collectLatest { talkHighlights ->
                    _uiState.update { it.copy(highlights = talkHighlights) }
                }
        }
    }

    fun finishPlayer() {
//        pauseRecord()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}