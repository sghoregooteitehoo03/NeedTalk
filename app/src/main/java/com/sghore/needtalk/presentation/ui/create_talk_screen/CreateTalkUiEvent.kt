package com.sghore.needtalk.presentation.ui.create_talk_screen


sealed interface CreateTalkUiEvent {
    data object ClickBackArrow : CreateTalkUiEvent

    data class ClickComplete(val selectedTime: Long) : CreateTalkUiEvent

    data class ClickAllowTimer(val isAllow: Boolean) : CreateTalkUiEvent

    data class ClickAllowMic(val isAllow: Boolean) : CreateTalkUiEvent

    data object ClickDecreasePeople : CreateTalkUiEvent

    data object ClickIncreasePeople : CreateTalkUiEvent

    data class ErrorMessage(val message: String) : CreateTalkUiEvent

}