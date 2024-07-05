package com.sghore.needtalk.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TalkHistory(
    val id: String,
    val talkTitle: String,
    val talkTime: Long,
    val recordFilePath: String,
    val recordFileSize: Long,
    val users: List<UserData>,
    val clipCount: Int,
    val createTimeStamp: Long
)
