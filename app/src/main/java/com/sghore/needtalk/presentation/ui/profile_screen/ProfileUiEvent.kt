package com.sghore.needtalk.presentation.ui.profile_screen

import com.sghore.needtalk.domain.model.Friend

sealed interface ProfileUiEvent {
    data object ClickNavigateUp : ProfileUiEvent

    data object ClickEditProfile : ProfileUiEvent

    data class ClickRemoveProfile(val friend: Friend) : ProfileUiEvent
}