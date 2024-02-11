package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TalkTopicEntity(
    @PrimaryKey
    val topic: String,
    val createTime: Long
)
