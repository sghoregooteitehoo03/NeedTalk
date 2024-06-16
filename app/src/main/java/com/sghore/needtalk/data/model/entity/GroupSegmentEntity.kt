package com.sghore.needtalk.data.model.entity

import androidx.room.Entity

@Entity(primaryKeys = ["groupId", "topicId"])
data class GroupSegmentEntity(
    val groupId: Int,
    val topicId: String,
    val isPublic: Boolean
)
