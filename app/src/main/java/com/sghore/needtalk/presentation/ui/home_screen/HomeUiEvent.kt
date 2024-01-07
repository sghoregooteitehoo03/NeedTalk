package com.sghore.needtalk.presentation.ui.home_screen

sealed interface HomeUiEvent {
    data object ClickNameTag : HomeUiEvent
    data object SuccessUpdateUserName : HomeUiEvent

    data object ClickStartAndClose : HomeUiEvent
}