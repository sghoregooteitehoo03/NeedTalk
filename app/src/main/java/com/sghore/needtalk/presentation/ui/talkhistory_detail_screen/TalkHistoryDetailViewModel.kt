package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import android.media.MediaPlayer
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
                isJumping = false
            )
        }
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

        mediaPlayer.seekTo(jumpTime.toInt())
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