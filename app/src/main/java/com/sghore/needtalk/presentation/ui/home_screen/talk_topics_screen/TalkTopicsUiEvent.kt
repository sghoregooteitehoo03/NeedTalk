package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import com.sghore.needtalk.domain.model.TalkTopicCategory
import com.sghore.needtalk.domain.model.TalkTopicGroup

sealed interface TalkTopicsUiEvent {
    data object ClickAddTopic : TalkTopicsUiEvent
    data class ClickTopicCategory(val category: TalkTopicCategory) : TalkTopicsUiEvent

    data class ClickTalkTopic(val index: Int) : TalkTopicsUiEvent

    data object ClickPopularMore : TalkTopicsUiEvent

    data object ClickGroupMore : TalkTopicsUiEvent

    data class ClickGroup(val group: TalkTopicGroup) : TalkTopicsUiEvent
}