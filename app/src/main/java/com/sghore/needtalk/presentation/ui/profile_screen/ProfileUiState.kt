package com.sghore.needtalk.presentation.ui.profile_screen

import com.sghore.needtalk.domain.model.Friend

data class ProfileUiState(
    val friends: List<Friend> = listOf(),
    val isLoading: Boolean = true
)
