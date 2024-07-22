package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen.talk_highlight_dialog

import com.sghore.needtalk.domain.model.TalkHighlight

data class TalkHighlightUiState(
    val highlights: List<TalkHighlight>? = null,
    val isPlaying: Boolean = false,
    val playIdx: Int = -1,
    val playerTime: Long = 0L
)
