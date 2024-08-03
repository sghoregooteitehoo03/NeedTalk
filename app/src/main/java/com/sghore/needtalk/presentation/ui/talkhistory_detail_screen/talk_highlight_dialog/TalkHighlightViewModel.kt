package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen.talk_highlight_dialog

import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.TalkHighlight
import com.sghore.needtalk.domain.usecase.DeleteTalkHighlightUseCase
import com.sghore.needtalk.domain.usecase.GetTalkHighlightUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TalkHighlightViewModel @Inject constructor(
    private val getTalkHighlightUseCase: GetTalkHighlightUseCase,
    private val deleteTalkHighlightUseCase: DeleteTalkHighlightUseCase
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

    // Player Job
    private var playerJob: Job? = null


    // initState
    fun initState(talkHistoryId: String) = viewModelScope.launch {
        if (_uiState.value.highlights == null) {
            getTalkHighlightUseCase(talkHistoryId)
                .collectLatest { talkHighlights ->
                    _uiState.update { it.copy(highlights = talkHighlights) }
                }
        }
    }

    fun clearState() {
        _uiState.update { TalkHighlightUiState() } // State 초기화
    }

    fun initMediaPlayer() {
        mediaPlayer = MediaPlayer()
        mediaPlayer?.setOnCompletionListener {
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    playIdx = -1
                )
            }

            playerJob?.cancel()
            playerJob = null
        }
    }

    fun setDialog(dialogScreen: DialogScreen) {
        _uiState.update { it.copy(dialogScreen = dialogScreen) }
    }

    fun removeTalkHighlight(talkHighlight: TalkHighlight) = viewModelScope.launch {
        deleteTalkHighlightUseCase(
            talkHighlight.id,
            talkHighlight.file
        )
    }

    fun playRecord(recordFilePath: String, index: Int) = viewModelScope.launch {
        if (_uiState.value.playIdx != index) {
            pauseRecord()
            _uiState.update {
                it.copy(
                    playerTime = 0L,
                    playIdx = index
                )
            }

            mediaPlayer?.reset()
            mediaPlayer?.setDataSource(recordFilePath)
            mediaPlayer?.prepare()
        }

        mediaPlayer?.start()
        _uiState.update { it.copy(isPlaying = true) }

        playerJob?.cancel()
        playerJob = viewModelScope.launch(context = Dispatchers.Default) {
            while (this.isActive) {
                _uiState.update {
                    it.copy(playerTime = mediaPlayer?.currentPosition?.toLong() ?: 0)
                }
            }
        }
    }

    fun pauseRecord() {
        mediaPlayer?.pause()
        _uiState.update { it.copy(isPlaying = false) }

        playerJob?.cancel()
        playerJob = null
    }

    fun seekPlayer(time: Long) {
        if (_uiState.value.isPlaying) {
            pauseRecord()
        }
        mediaPlayer?.seekTo(time.toInt())
        _uiState.update { it.copy(playerTime = mediaPlayer?.currentPosition?.toLong() ?: 0L) }
    }

    fun finishPlayer() {
        pauseRecord()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}