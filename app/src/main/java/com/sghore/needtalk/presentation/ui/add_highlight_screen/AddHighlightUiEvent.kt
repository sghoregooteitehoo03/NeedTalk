package com.sghore.needtalk.presentation.ui.add_highlight_screen

sealed interface AddHighlightUiEvent {
    data object ClickNavigateUp : AddHighlightUiEvent

    data class ChangeTitle(val title: String) : AddHighlightUiEvent

    data class ChangePlayerTime(val startTime: Long, val endTime: Long) : AddHighlightUiEvent

    data class ClickPlayOrPause(val isPlay: Boolean) : AddHighlightUiEvent

    data object ClickComplete : AddHighlightUiEvent

    data class AlertError(val message: String) : AddHighlightUiEvent

    data object SuccessAddHighlight : AddHighlightUiEvent
}