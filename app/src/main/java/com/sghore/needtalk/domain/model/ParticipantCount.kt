package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity

data class ParticipantCount(
    val userEntity: UserEntity,
    val count: Int
)