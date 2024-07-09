package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import com.sghore.needtalk.domain.model.TalkHistory

data class TalkHistoryDetailUiState(
    val talkHistory: TalkHistory? = null,
    val playerTime: Long = 0L,
    val isPlaying: Boolean = false
)
