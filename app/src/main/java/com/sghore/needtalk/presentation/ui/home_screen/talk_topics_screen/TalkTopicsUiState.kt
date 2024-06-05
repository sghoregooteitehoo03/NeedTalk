package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import com.sghore.needtalk.domain.model.TalkTopic

data class TalkTopicsUiState(
    val popularTalkTopics: List<TalkTopic> = listOf()
)
