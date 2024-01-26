package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity

data class ParticipantInfo(
    val userEntity: UserEntity,
    val endpointId: String
)
