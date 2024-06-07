package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.TalkTopicGroup

data class TalkTopicsUiState(
    val isLoading: Boolean = true,
    val popularTalkTopics: List<TalkTopic> = listOf(),
    val talkTopicGroups: List<TalkTopicGroup> = listOf()
)
