package com.sghore.needtalk.presentation.ui.statics_screen

sealed interface StaticsUiEvent {
    data object ClickBackArrow : StaticsUiEvent

    data class ClickChangeDate(val startTime: Long, val endTime: Long) : StaticsUiEvent
}