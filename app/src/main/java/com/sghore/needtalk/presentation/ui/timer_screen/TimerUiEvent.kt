package com.sghore.needtalk.presentation.ui.timer_screen

interface TimerUiEvent {
    data object ClickExit : TimerUiEvent

    data class ClickStart(val isEnabled: Boolean) : TimerUiEvent

    data object ClickFinished : TimerUiEvent

    data object AddPinnedTalkTopic : TimerUiEvent

    data object CancelPinnedTopic : TimerUiEvent
}