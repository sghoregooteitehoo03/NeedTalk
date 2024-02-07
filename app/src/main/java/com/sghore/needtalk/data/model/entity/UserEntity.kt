package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val color: Int,
    val createTime: Long = System.currentTimeMillis()
)
