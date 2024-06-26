package com.sghore.needtalk.presentation.ui.join_talk_screen

import com.sghore.needtalk.domain.model.TimerInfo

sealed interface JoinTalkUiEvent {
    data object ClickBackArrow : JoinTalkUiEvent

    data class ClickJoin(val hostEndPointId: String) : JoinTalkUiEvent

    data object ClickResearch : JoinTalkUiEvent

    data class LoadTimerInfo(val index: Int) : JoinTalkUiEvent

    data class ShowSnackBar(val message: String) : JoinTalkUiEvent
}