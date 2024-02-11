package com.sghore.needtalk.presentation.ui.join_screen

import com.sghore.needtalk.domain.model.TimerInfo

sealed interface JoinUiEvent {
    data object ClickBackArrow : JoinUiEvent

    data class ClickJoin(val timerInfo: TimerInfo) : JoinUiEvent

    data object ClickResearch : JoinUiEvent

    data class LoadTimerInfo(val index: Int) : JoinUiEvent

    data class ShowSnackBar(val message: String) : JoinUiEvent
}