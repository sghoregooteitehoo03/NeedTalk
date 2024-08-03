package com.sghore.needtalk.domain.model

import java.io.File

data class TalkHistory(
    val id: String,
    val talkTitle: String,
    val talkTime: Long,
    val recordFile: File?,
    val recordAmplitude: List<Int>,
    val users: List<UserData>,
    val clipCount: Int,
    val createTimeStamp: Long
)
