package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

sealed interface TalkTopicsDetailUiEvent {
    data object ClickNavigateUp : TalkTopicsDetailUiEvent

    data class SelectOrderType(val orderType: OrderType) : TalkTopicsDetailUiEvent

    data class ClickFavorite(val topicId: String, val isFavorite: Boolean) : TalkTopicsDetailUiEvent

    data object ClickBookmark : TalkTopicsDetailUiEvent
}