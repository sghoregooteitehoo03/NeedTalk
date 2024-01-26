package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity

data class TimerInfo(
    val hostUser: UserEntity,
    val timerTime: Long,
    val currentMember: Int,
    val maxMember: Int,
    val hostEndpointId: String = ""
)