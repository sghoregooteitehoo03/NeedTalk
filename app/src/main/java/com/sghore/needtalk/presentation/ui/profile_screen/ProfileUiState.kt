package com.sghore.needtalk.presentation.ui.profile_screen

import com.sghore.needtalk.domain.model.UserData

data class ProfileUiState(
    val friends: List<UserData> = listOf(),
    val isLoading: Boolean = true
)
