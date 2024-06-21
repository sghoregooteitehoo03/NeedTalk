package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: userName, profileImage 삭제하기
@Entity
data class FriendEntity(
    @PrimaryKey
    val userId: String,
    val userName: String,
    val profileImage: ByteArray,
    val experiencePoint: Int,
    val friendshipPoint: Int,
    val createTime: Long = System.currentTimeMillis()
)
