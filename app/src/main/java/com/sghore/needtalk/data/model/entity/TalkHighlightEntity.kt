package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TalkHighlightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val filePath: String,
    val timestamp: Long = System.currentTimeMillis()
)
