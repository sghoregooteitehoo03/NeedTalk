package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity

data class TalkHistory(
    val talkTime: Long,
    val users: List<UserEntity?>,
    val createTimeStamp: Long
)
