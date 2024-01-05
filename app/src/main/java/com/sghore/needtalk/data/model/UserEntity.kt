package com.sghore.needtalk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val color: Int
)
