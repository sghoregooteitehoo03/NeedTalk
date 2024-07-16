package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.presentation.ui.DialogScreen
import java.io.File

data class TalkHistoryDetailUiState(
    val talkHistory: TalkHistory? = null,
    val recordFile: File? = null,
    val recordWaveForm: List<Int> = emptyList(),
    val playerTime: Long = 0L,
    val isPlaying: Boolean = false,
    val isSeeking: Boolean = false,
    val isJumping: Boolean = false,
    val isComplete: Boolean = false,
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss
)
