package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TalkHistoryEntity(
    @PrimaryKey
    val id: String,
    val talkTitle: String,
    val talkTime: Long,
    val recordFilePath: String,
    val recordFileSize: Long,
    val createTimeStamp: Long = System.currentTimeMillis()
)
