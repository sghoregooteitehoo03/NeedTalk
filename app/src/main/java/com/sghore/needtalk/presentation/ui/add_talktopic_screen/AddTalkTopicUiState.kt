package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import com.sghore.needtalk.domain.model.TalkTopicCategory

data class AddTalkTopicUiState(
    val talkTopicText: String = "",
    val selectedCategories: List<TalkTopicCategory> = emptyList(),
    val selectedCount: Int = 0,
    val isPublic: Boolean = true
)
