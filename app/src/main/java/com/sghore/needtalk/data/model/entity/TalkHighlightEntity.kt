package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TalkHighlightEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val title: String,
    val filePath: String,
    val duration: Int,
    val talkHistoryId: String,
    val timestamp: Long = System.currentTimeMillis()
)
