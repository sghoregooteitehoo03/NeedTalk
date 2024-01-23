package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class TimerInfo(
    val userList: List<UserEntity>,
    val timerTime: Long,
    val maxMember: Int,
    val endpointId: String = ""
)
