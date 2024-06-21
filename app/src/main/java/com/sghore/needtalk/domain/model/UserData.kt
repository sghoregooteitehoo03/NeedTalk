package com.sghore.needtalk.domain.model

import androidx.compose.ui.graphics.ImageBitmap

data class UserData(
    val userId: String,
    val name: String,
    val profileImage: ImageBitmap,
    val experiencePoint: Int,
    val friendshipPoint: Int
)