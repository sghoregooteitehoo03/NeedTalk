package com.sghore.needtalk.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ParticipantInfo(
    val userId: String,
    val name: String,
    val profileImage: ByteArray,
    val endpointId: String,
    val isReady: Boolean?
)
