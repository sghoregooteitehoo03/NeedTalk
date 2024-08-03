package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TalkHistoryParticipantEntity(
    @PrimaryKey
    val id: Int? = null,
    val talkHistoryId: String,
    val userId: String,
    val friendshipPoint: Int,
)