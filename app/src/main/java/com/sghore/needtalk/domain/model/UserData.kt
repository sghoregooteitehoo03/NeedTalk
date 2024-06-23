package com.sghore.needtalk.domain.model

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userId: String,
    val name: String,
    val profileImage: ImageBitmap,
    val experiencePoint: Int,
    val friendshipPoint: Int
)