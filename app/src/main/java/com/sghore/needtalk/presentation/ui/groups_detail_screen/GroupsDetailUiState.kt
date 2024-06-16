package com.sghore.needtalk.presentation.ui.groups_detail_screen

import com.sghore.needtalk.domain.model.TalkTopicGroup

data class GroupsDetailUiState(
    val groups: List<TalkTopicGroup> = emptyList()
)
