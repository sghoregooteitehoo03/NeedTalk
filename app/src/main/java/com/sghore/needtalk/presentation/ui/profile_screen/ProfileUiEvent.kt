package com.sghore.needtalk.presentation.ui.profile_screen

import com.sghore.needtalk.domain.model.UserData

sealed interface ProfileUiEvent {
    data object ClickNavigateUp : ProfileUiEvent

    data object ClickEditProfile : ProfileUiEvent

    data class ClickRemoveFriend(val friend: UserData) : ProfileUiEvent
}