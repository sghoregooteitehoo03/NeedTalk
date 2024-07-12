package com.sghore.needtalk.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TalkResult(
    val talkTime: Long,
    val recordFilePath: String,
    val recordAmplitude: List<Int>,
    val userTalkResult: List<UserTalkResult>,
)
