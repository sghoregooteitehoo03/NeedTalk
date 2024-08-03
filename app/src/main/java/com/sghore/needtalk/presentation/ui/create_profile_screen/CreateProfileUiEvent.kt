package com.sghore.needtalk.presentation.ui.create_profile_screen

import android.graphics.Bitmap
import com.sghore.needtalk.domain.model.UserData

sealed interface CreateProfileUiEvent {

    data class ChangeName(val name: String) : CreateProfileUiEvent

    data class SelectProfileImage(val type: ProfileType, val imageIndex: Int) : CreateProfileUiEvent

    data class ClickConfirm(
        val userId: String,
        val faceImage: Bitmap,
        val hairImage: Bitmap,
        val accessoryImage: Bitmap
    ) : CreateProfileUiEvent
}

sealed class ProfileType {
    data object Face : ProfileType()
    data object Hair : ProfileType()
    data object Accessory : ProfileType()
}