package com.sghore.needtalk.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantInfo(
    val userData: UserData,
    val endpointId: String,
    val isReady: Boolean?
)
