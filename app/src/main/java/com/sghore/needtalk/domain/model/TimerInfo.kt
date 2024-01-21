package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity

data class TimerInfo(
    val userEntity: UserEntity,
    val timerTime: Long,
    val member: Int,
    val maxMember: Int
)
