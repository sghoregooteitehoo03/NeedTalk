package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import com.sghore.needtalk.domain.model.TalkTopicCategory

data class AddTalkTopicUiState(
    val talkTopic: String = "",
    val selectedCategory1: TalkTopicCategory? = null,
    val selectedCategory2: TalkTopicCategory? = null,
    val selectedCategory3: TalkTopicCategory? = null,
    val selectedCount: Int = 0,
    val isPublic: Boolean = true
)
