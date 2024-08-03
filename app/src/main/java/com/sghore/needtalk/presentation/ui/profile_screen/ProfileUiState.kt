package com.sghore.needtalk.presentation.ui.profile_screen

import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.DialogScreen

data class ProfileUiState(
    val friends: List<UserData> = listOf(),
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss,
    val isLoading: Boolean = true
)
