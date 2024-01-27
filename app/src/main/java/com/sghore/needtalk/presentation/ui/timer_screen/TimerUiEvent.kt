package com.sghore.needtalk.presentation.ui.timer_screen

interface TimerUiEvent {
    data object ClickExit : TimerUiEvent
    data class ClickStart(val isEnabled: Boolean) : TimerUiEvent
}