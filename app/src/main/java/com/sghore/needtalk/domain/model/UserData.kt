package com.sghore.needtalk.domain.model

import android.graphics.Bitmap

data class UserData(
    val userId: String,
    val name: String,
    val profileImage: Bitmap,
)
