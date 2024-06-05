package com.sghore.needtalk.domain.model

data class TalkTopic(
    val topicId: String,
    val uid: String,
    val topic: String,
    val favoriteCount: Int,
    val favorites: HashMap<String, Boolean>,
    val isUpload: Boolean,
    val category1: TalkTopicCategory,
    val category2: TalkTopicCategory? = null,
    val category3: TalkTopicCategory? = null,
)