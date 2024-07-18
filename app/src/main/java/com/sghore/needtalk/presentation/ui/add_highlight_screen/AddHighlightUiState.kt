package com.sghore.needtalk.presentation.ui.add_highlight_screen

import java.io.File

data class AddHighlightUiState(
    val title: String = "",
    val recordFile: File? = null,
    val recordAmplitude: List<Int> = emptyList(),
    val playerTime: Long = 0L,
    val playerMaxTime: Long = 0L,
    val cutStartTime: Long = 0L,
    val cutEndTime: Long = cutStartTime + 30000L,
    val isPlaying: Boolean = false,
    val isSeeking: Boolean = false,
    val isComplete: Boolean = false,
)
