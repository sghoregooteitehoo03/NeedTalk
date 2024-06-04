package com.sghore.needtalk.domain.model

data class TalkTopic(
    val topicId: String,
    val topic: String,
    val favoriteCount: Int,
    val isFavorite: Boolean,
    val category1: String,
    val category2: String = "",
    val category3: String = "",
)