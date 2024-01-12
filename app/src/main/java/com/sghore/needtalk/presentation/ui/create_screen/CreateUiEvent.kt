package com.sghore.needtalk.presentation.ui.create_screen

sealed interface CreateUiEvent {
    data object ClickBackArrow : CreateUiEvent
    data class ChangeTime(val talkTime: Long) : CreateUiEvent
    data class ClickStopWatchMode(val isAllow: Boolean) : CreateUiEvent

    data object ClickAddMusic : CreateUiEvent

    data class ClickAllowRepeatMusic(val isAllow: Boolean) : CreateUiEvent

    data class ClickNumberOfPeople(val number: Int) : CreateUiEvent

}