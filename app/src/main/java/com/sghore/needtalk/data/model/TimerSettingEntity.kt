package com.sghore.needtalk.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

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