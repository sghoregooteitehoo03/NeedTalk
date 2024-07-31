package com.sghore.needtalk.presentation.ui.result_screen

sealed interface ResultUiEvent {
    data object ClickConfirm : ResultUiEvent

    data class AddFriend(val userId: String, val index: Int) : ResultUiEvent

    data class ChangeTalkTitle(val title: String) : ResultUiEvent

    data class AnimationEnd(val index: Int) : ResultUiEvent
}