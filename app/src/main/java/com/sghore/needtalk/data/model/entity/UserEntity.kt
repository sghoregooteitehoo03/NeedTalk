package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val profileImage: ByteArray,
    val selectedFaceImageRes: Int,
    val selectedHairImageRes: Int,
    val selectedAccessoryImageRes: Int,
    val createTime: Long = System.currentTimeMillis()
)
