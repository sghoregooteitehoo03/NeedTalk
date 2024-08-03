package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class TalkSettingEntity(
    @PrimaryKey
    val userId: String,
    val talkTime: Long,
    val isTimer: Boolean,
    val isAllowMic: Boolean,
    val numberOfPeople: Int
)