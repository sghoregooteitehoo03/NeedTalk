package com.sghore.needtalk.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserTalkResult(
    val userId: String,
    val talkTime: Long,
    val experiencePoint: Double,
)
