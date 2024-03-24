package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity
import kotlinx.serialization.Serializable

@Serializable
data class PinnedTalkTopic(
    val talkTopic: String,
    val pinnedUser: UserEntity
)
