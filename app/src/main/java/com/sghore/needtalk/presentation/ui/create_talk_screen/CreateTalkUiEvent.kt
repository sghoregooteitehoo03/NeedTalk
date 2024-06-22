package com.sghore.needtalk.presentation.ui.create_talk_screen


sealed interface CreateTalkUiEvent {
    data object ClickBackArrow : CreateTalkUiEvent

    data object ClickComplete : CreateTalkUiEvent

    data class ClickStopWatchMode(val isAllow: Boolean) : CreateTalkUiEvent

    data class ClickNumberOfPeople(val number: Int) : CreateTalkUiEvent

    data class ErrorMessage(val message: String) : CreateTalkUiEvent

}