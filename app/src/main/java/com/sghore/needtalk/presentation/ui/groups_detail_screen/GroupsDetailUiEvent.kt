package com.sghore.needtalk.presentation.ui.groups_detail_screen

sealed interface GroupsDetailUiEvent {
    data object ClickNavigateUp : GroupsDetailUiEvent

    data object SelectRemove : GroupsDetailUiEvent

    data object SelectEdit : GroupsDetailUiEvent

    data object ClickGroupItem : GroupsDetailUiEvent
}