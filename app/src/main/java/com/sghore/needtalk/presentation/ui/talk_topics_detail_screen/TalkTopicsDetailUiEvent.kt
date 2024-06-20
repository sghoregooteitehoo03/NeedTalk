package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import com.sghore.needtalk.domain.model.TalkTopic

sealed interface TalkTopicsDetailUiEvent {
    data object ClickNavigateUp : TalkTopicsDetailUiEvent

    data class SelectOrderType(val orderType: OrderType) : TalkTopicsDetailUiEvent

    data class ClickFavorite(val topicId: String, val isFavorite: Boolean) : TalkTopicsDetailUiEvent

    data class ClickBookmark(val talkTopic: TalkTopic) : TalkTopicsDetailUiEvent

    data class ClickRemove(val talkTopic: TalkTopic) : TalkTopicsDetailUiEvent
}