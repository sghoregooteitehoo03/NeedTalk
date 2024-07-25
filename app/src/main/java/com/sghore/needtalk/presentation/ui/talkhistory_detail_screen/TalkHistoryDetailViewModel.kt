package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.usecase.DeleteTalkHistoryUseCase
import com.sghore.needtalk.domain.usecase.GetTalkHistoryUseCase
import com.sghore.needtalk.domain.usecase.InsertTalkHistoryUseCase
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
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TalkHistoryDetailViewModel @Inject constructor(
    private val getTalkHistoryUseCase: GetTalkHistoryUseCase,
    private val insertTalkHistoryUseCase: InsertTalkHistoryUseCase,
    private val deleteTalkHistoryUseCase: DeleteTalkHistoryUseCase,
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
    private var mediaPlayer: MediaPlayer? = null

    // Player Job
    private var playerJob: Job? = null

    init {
        val talkHistoryId = savedStateHandle.get<String>("talkHistoryId") ?: ""
        if (talkHistoryId.isNotEmpty()) {
            viewModelScope.launch {
                val talkHistory = getTalkHistoryUseCase(talkHistoryId)

                try {
                    preparePlayer(talkHistory?.recordFile?.path ?: "")
                    _uiState.update {
                        it.copy(
                            talkHistory = talkHistory?.copy(
                                talkTime = mediaPlayer?.duration?.toLong() ?: 0
                            )
                        )
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    // 다이얼로그 화면 설정
    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update { it.copy(dialogScreen = dialogScreen) }
    }

    // 대화기록 제목 수정
    fun updateTitle(title: String) = viewModelScope.launch {
        val updateTalkHistory = _uiState.value.talkHistory?.copy(talkTitle = title)

        insertTalkHistoryUseCase(updateTalkHistory)
        _uiState.update { it.copy(talkHistory = updateTalkHistory) }
    }

    // 대화기록 삭제
    fun removeTalkHistory() = viewModelScope.launch {
        val talkHistory = _uiState.value.talkHistory

        if (talkHistory != null) {
            try {
                deleteTalkHistoryUseCase(talkHistory)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Player Seek Start/Pause
    fun seekPlayer(isSeeking: Boolean) {
        val isPlaying = _uiState.value.isPlaying
        if (isPlaying && isSeeking) {
            pauseRecord()
        }

        _uiState.update {
            it.copy(
                isSeeking = isSeeking,
                isJumping = false,
                isComplete = false
            )
        }
    }

    // Player seek
    fun changeTime(changeTime: Long) {
        val isSeeking = _uiState.value.isSeeking

        if (isSeeking) {
            _uiState.update { it.copy(playerTime = changeTime) }
        }
    }

    fun playRecord() {
        if (!_uiState.value.isComplete) {
            mediaPlayer?.seekTo(_uiState.value.playerTime.toInt())
        }

        mediaPlayer?.start()
        _uiState.update {
            it.copy(
                isPlaying = true,
                isComplete = false,
                isSeeking = false,
                isJumping = false
            )
        }

        playerJob?.cancel()
        playerJob = viewModelScope.launch(context = Dispatchers.Default) {
            var oldTimeMills = System.currentTimeMillis()

            while (this.isActive) {
                val duration = _uiState.value.talkHistory?.talkTime ?: 0L
                if (_uiState.value.playerTime == duration) {
                    _uiState.update { it.copy(isComplete = true) }
                    pauseRecord()
                }

                val delayMills = System.currentTimeMillis() - oldTimeMills
                if (delayMills >= 50) {
                    oldTimeMills = System.currentTimeMillis()
                    val addTime =
                        if ((duration - _uiState.value.playerTime) < 50) {
                            duration - _uiState.value.playerTime
                        } else {
                            50
                        }

                    _uiState.update {
                        it.copy(playerTime = it.playerTime + addTime)
                    }
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

    fun preparePlayer(recordFilePath: String) {
        if (mediaPlayer == null) {
            try {// 미디어 플레이어 정의
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setDataSource(recordFilePath)
                mediaPlayer?.prepare()

                val playerTime = _uiState.value.playerTime
                if (playerTime != 0L) { // PlayerTime이 0이 아닐 때
                    mediaPlayer?.seekTo(playerTime.toInt())
                }
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    fun finishPlayer() {
        pauseRecord()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    // 건너뛰기
    fun jumpToSecond(second: Int) {
        val currentTime = _uiState.value.playerTime
        val maxTime = _uiState.value.talkHistory?.talkTime ?: 0L
        var jumpTime = currentTime + second

        if (jumpTime < 0) {
            jumpTime = 0
        } else if (maxTime < jumpTime) {
            jumpTime = maxTime
        }

        mediaPlayer?.seekTo(jumpTime.toInt())
        _uiState.update {
            it.copy(
                playerTime = jumpTime,
                isJumping = true
            )
        }
    }

    fun handelEvent(event: TalkHistoryDetailUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}