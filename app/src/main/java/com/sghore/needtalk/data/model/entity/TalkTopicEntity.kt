package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class TalkTopicEntity(
    @PrimaryKey
    val topic: String,
    val createTime: Long
)
