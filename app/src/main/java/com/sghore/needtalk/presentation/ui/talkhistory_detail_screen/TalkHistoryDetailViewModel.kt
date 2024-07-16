package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.domain.usecase.GetTalkHistoryUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TalkHistoryDetailViewModel @Inject constructor(
    private val getTalkHistoryUseCase: GetTalkHistoryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(TalkHistoryDetailUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TalkHistoryDetailUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<TalkHistoryDetailUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly
    )

    // MediaPlayer
    private val mediaPlayer = MediaPlayer()

    // Player Job
    private var playerJob: Job? = null

    init {
        val talkHistoryId = savedStateHandle.get<String>("talkHistoryId") ?: ""
        if (talkHistoryId.isNotEmpty()) {
            viewModelScope.launch {
                val talkHistory = getTalkHistoryUseCase(talkHistoryId)

                try {
                    // 미디어 플레이어 정의
                    mediaPlayer.setDataSource(talkHistory?.recordFile?.path)
                    mediaPlayer.prepare()

                    mediaPlayer.setOnPreparedListener {
                        _uiState.update {
                            it.copy(talkHistory = talkHistory?.copy(talkTime = mediaPlayer.duration.toLong()))
                        }
                        initListener() // 리스너 초기화
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // 리스너 초기화
    private fun initListener() {
        mediaPlayer.setOnCompletionListener {
            _uiState.update {
                it.copy(
                    isPlaying = false,
                    isComplete = true
                )
            }

            playerJob?.cancel()
            playerJob = null
        }
    }

    // 다이얼로그 화면 설정
    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update { it.copy(dialogScreen = dialogScreen) }
    }

    // Player Seek Start/Pause
    fun seekPlayer(isSeeking: Boolean) {
        val isPlaying = _uiState.value.isPlaying
        if (isPlaying && isSeeking) {
            pauseRecord()
        }

        _uiState.update { it.copy(isSeeking = isSeeking) }
    }

    // Player seek
    fun changeTime(changeTime: Long) {
        val isSeeking = _uiState.value.isSeeking

        if (isSeeking) {
            mediaPlayer.seekTo(changeTime.toInt())
            _uiState.update { it.copy(playerTime = changeTime) }
        }
    }

    fun playRecord() {
        mediaPlayer.start()
        _uiState.update { it.copy(isPlaying = true, isComplete = false, isSeeking = false) }

        playerJob?.cancel()
        playerJob = viewModelScope.launch(context = Dispatchers.Default) {
            while (this.isActive) {
                _uiState.update {
                    it.copy(
                        playerTime = mediaPlayer.currentPosition.toLong()
                    )
                }
            }
        }
    }

    fun pauseRecord() {
        mediaPlayer.pause()
        _uiState.update { it.copy(isPlaying = false) }

        playerJob?.cancel()
        playerJob = null
    }

    fun finishPlayer() {
        mediaPlayer.stop()
        mediaPlayer.release()
    }

    fun handelEvent(event: TalkHistoryDetailUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}