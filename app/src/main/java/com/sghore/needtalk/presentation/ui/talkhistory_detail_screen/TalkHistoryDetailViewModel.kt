package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.presentation.ui.DialogScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class TalkHistoryDetailViewModel @Inject constructor(
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

    init {
        val talkHistoryJson = savedStateHandle.get<String>("talkHistory") ?: ""
        if (talkHistoryJson.isNotEmpty()) {
            val talkHistory = Json.decodeFromString(TalkHistory.serializer(), talkHistoryJson)

            // 녹음 파일을 가져옴
            val file = File(talkHistory.recordFilePath)
            val samples = extractWaveform(file)

            _uiState.update {
                it.copy(
                    talkHistory = talkHistory,
                    recordFile = file,
                    recordWaveForm = samples
                )
            }
        }
    }

    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update { it.copy(dialogScreen = dialogScreen) }
    }

    fun handelEvent(event: TalkHistoryDetailUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    private fun extractWaveform(file: File): List<Int> {
        val samples = mutableListOf<Int>()
        val fileInputStream = FileInputStream(file)

        try {
            val byteArray = fileInputStream.readBytes()

            for (i in byteArray.indices step 2) {
                val sample =
                    (byteArray[i].toInt() and 0xff shl 8) or (byteArray[i + 1].toInt() and 0xff)
                samples.add(sample)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            fileInputStream.close()
        }

        return samples.filterNot { it == 0 }.chunked(4410).map { it.max() }
    }
}