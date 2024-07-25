package com.sghore.needtalk.presentation.ui.add_highlight_screen

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.usecase.AddHighlightUseCase
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
    private val addHighlightUseCase: AddHighlightUseCase,
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
    private var mediaPlayer: MediaPlayer? = null

    // Player Job
    private var playerJob: Job? = null

    private var isFirst: Boolean = true
    private var talkHistoryId: String = ""

    init {
        talkHistoryId = savedStateHandle.get<String>("talkHistoryId") ?: ""
        val recodeFilePath = savedStateHandle.get<String>("recordFilePath") ?: ""
        val recordAmplitudeJson = savedStateHandle.get<String>("recordAmplitude") ?: ""

        if (talkHistoryId.isNotEmpty() && recodeFilePath.isNotEmpty() && recordAmplitudeJson.isNotEmpty()) {
            val recordFile = File(recodeFilePath)
            val recordAmplitude = Json.decodeFromString<List<Int>>(recordAmplitudeJson)

            preparePlayer(recordFile.path)
            mediaPlayer?.setOnPreparedListener {
                if (_uiState.value.recordFile == null) {
                    _uiState.update {
                        it.copy(
                            recordFile = recordFile,
                            recordAmplitude = recordAmplitude,
                            playerMaxTime = mediaPlayer?.duration?.toLong() ?: 0L
                        )
                    }
                }
            }
        }
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

    fun handelEvent(event: AddHighlightUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    // 타이틀 변경
    fun changeTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }

    // 하이라이트 생성
    fun addHighlight(directoryPath: String) = viewModelScope.launch {
        if (isFirst) {
            isFirst = false // 두번 동작 안되게

            val stateValue = _uiState.value
            addHighlightUseCase(
                recordFilePath = stateValue.recordFile?.path ?: "",
                directoryPath = directoryPath,
                title = stateValue.title,
                startTime = stateValue.cutStartTime.toInt(),
                duration = (stateValue.cutEndTime.toInt() - stateValue.cutStartTime.toInt()),
                talkHistoryId = talkHistoryId,
                onSuccess = { handelEvent(AddHighlightUiEvent.SuccessAddHighlight) },
                onError = {
                    isFirst = true
                    handelEvent(AddHighlightUiEvent.AlertError(it))
                }
            )
        }
    }

    // Cut Change
    fun changeTime(startTime: Long, endTime: Long) {
        if (_uiState.value.isPlaying) {
            pauseRecord()
        }

        mediaPlayer?.seekTo(startTime.toInt())
        _uiState.update {
            it.copy(
                playerTime = startTime,
                cutStartTime = startTime,
                cutEndTime = endTime
            )
        }
    }

    fun playRecord() {
        if (_uiState.value.isComplete) {
            mediaPlayer?.seekTo(_uiState.value.cutStartTime.toInt())
        }

        mediaPlayer?.start()
        _uiState.update {
            it.copy(
                isPlaying = true,
                isComplete = false
            )
        }

        playerJob?.cancel()
        playerJob = viewModelScope.launch(context = Dispatchers.Default) {
            var oldTimeMills = System.currentTimeMillis()

            while (this.isActive) {
                val duration = _uiState.value.playerMaxTime
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

    fun finishPlayer() {
        pauseRecord()
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}