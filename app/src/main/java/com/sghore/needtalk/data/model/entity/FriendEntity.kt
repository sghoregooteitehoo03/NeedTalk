package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FriendEntity(
    @PrimaryKey
    val userId: String,
    val experiencePoint: Float,
    val friendshipPoint: Int,
    val createTime: Long = System.currentTimeMillis()
)
