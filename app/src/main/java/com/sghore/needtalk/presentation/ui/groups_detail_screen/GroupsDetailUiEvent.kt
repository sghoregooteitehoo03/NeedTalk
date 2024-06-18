package com.sghore.needtalk.presentation.ui.groups_detail_screen

import com.sghore.needtalk.domain.model.TalkTopicGroup

sealed interface GroupsDetailUiEvent {
    data object ClickNavigateUp : GroupsDetailUiEvent

    data class SelectEdit(val group: TalkTopicGroup) : GroupsDetailUiEvent

    data class SelectRemove(val group: TalkTopicGroup) : GroupsDetailUiEvent

    data object ClickGroupItem : GroupsDetailUiEvent
}