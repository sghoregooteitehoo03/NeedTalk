package com.sghore.needtalk.presentation.ui.join_screen

sealed interface JoinUiEvent {
    data object ClickBackArrow : JoinUiEvent
    data class LoadTimerInfo(val index: Int) : JoinUiEvent
    data class ShowSnackBar(val message: String) : JoinUiEvent
}