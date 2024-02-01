package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class ParticipantInfo(
    val userEntity: UserEntity,
    val endpointId: String,
    val isReady: Boolean?
)
