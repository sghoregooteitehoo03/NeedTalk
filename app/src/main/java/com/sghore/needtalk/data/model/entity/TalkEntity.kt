package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TalkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val talkTime: Long,
    val usersId: String,
    val createTimeStamp: Long
)
