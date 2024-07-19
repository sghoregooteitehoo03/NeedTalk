package com.sghore.needtalk.presentation.ui.add_highlight_screen

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
@HiltViewModel
class AddHighlightViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(AddHighlightUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddHighlightUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<AddHighlightUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly
    )

    // MediaPlayer
    private val mediaPlayer = MediaPlayer()

    // Player Job
    private var playerJob: Job? = null

    init {
        val recodeFilePath = savedStateHandle.get<String>("recordFilePath") ?: ""
        val recordAmplitudeJson = savedStateHandle.get<String>("recordAmplitude") ?: ""

        if (recodeFilePath.isNotEmpty() && recordAmplitudeJson.isNotEmpty()) {
            val recordFile = File(recodeFilePath)
            val recordAmplitude = Json.decodeFromString<List<Int>>(recordAmplitudeJson)

            preparePlayer(recordFile.path)
            mediaPlayer.setOnPreparedListener {
                _uiState.update {
                    it.copy(
                        recordFile = recordFile,
                        recordAmplitude = recordAmplitude,
                        playerMaxTime = mediaPlayer.duration.toLong()
                    )
                }
                initListener() // 리스너 초기화
            }
        }
    }

    private fun preparePlayer(recordFilePath: String) {
        try {// 미디어 플레이어 정의
            mediaPlayer.setDataSource(recordFilePath)
            mediaPlayer.prepare()

            val playerTime = _uiState.value.playerTime
            if (playerTime != 0L) { // PlayerTime이 0이 아닐 때
                mediaPlayer.seekTo(playerTime.toInt())
            }
        } catch (e: IllegalStateException) {
            e.printStackTrace()
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

    fun handelEvent(event: AddHighlightUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    // 타이틀 변경
    fun changeTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    // Player Seek Start/Pause
    fun seekPlayer() {
        val isPlaying = _uiState.value.isPlaying
        if (isPlaying) {
            pauseRecord()
        }

        _uiState.update {
            it.copy(isSeeking = true)
        }
    }

    // Cut Change
    fun changeTime(startTime: Long, endTime: Long) {
        val isSeeking = _uiState.value.isSeeking

        if (isSeeking) {
            _uiState.update {
                it.copy(
                    playerTime = startTime,
                    cutStartTime = startTime,
                    cutEndTime = endTime
                )
            }
        }
    }

    fun playRecord() {
        mediaPlayer.start()
        _uiState.update {
            it.copy(
                isPlaying = true,
                isComplete = false,
                isSeeking = false
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
}