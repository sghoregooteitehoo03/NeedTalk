package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MusicEntity(
    @PrimaryKey
    val id: String,
    val thumbnailImage: String,
    val title: String,
    val timestamp: Long
)
