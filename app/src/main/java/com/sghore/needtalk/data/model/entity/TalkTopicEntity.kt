package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TalkTopicEntity(
    @PrimaryKey
    val id: String,
    val uid: String,
    val topic: String,
    val categoryCode1: Int,
    val categoryCode2: Int?,
    val categoryCode3: Int?,
    val createdTime: Long
)