package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.presentation.ui.DialogScreen
import java.io.File

data class TalkHistoryDetailUiState(
    val talkHistory: TalkHistory? = null,
    val recordFile: File? = null,
    val playerTime: Long = 0L,
    val isPlaying: Boolean = false,
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss
)
