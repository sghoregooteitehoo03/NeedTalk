package com.sghore.needtalk.presentation.ui.create_profile_screen

import com.sghore.needtalk.domain.model.UserData

data class CreateProfileUiState(
    val profileName: String = "",
    val selectedFaceIndex: Int = 0,
    val selectedHairStyleIndex: Int = 0,
    val selectedAccessoryIndex: Int = 0,
    val isUpdateProfile: Boolean = false
)
