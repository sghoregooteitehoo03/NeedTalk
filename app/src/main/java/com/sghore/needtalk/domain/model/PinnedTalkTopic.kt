package com.sghore.needtalk.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class PinnedTalkTopic(
    val talkTopic: TalkTopic,
    val pinnedUserId: String,
    val pinnedUserName: String
)
