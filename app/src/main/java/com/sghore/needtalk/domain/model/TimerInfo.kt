package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.MusicEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class TimerInfo(
    val userList: List<UserEntity>,
    val musicInfo: MusicEntity,
    val timerTime: Long,
    val maxMember: Int
)
