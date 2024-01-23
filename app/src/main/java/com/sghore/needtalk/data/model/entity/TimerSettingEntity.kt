package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Serializable
@Entity
data class TimerSettingEntity(
    @PrimaryKey
    val userId: String,
    val talkTime: Long,
    val isStopwatch: Boolean,
    val selectMusicId: String,
    val allowRepeatMusic: Boolean,
    val numberOfPeople: Int
)