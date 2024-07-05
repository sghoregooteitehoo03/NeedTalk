package com.sghore.needtalk.domain.model

import androidx.compose.ui.graphics.ImageBitmap
import com.sghore.needtalk.util.ImageBitmapSerializer
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val userId: String,
    val name: String,
    @Serializable(with = ImageBitmapSerializer::class) val profileImage: ImageBitmap,
    val experiencePoint: Float,
    val friendshipPoint: Int
)