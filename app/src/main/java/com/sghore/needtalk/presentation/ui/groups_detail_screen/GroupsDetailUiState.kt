package com.sghore.needtalk.presentation.ui.groups_detail_screen

import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.presentation.ui.DialogScreen

data class GroupsDetailUiState(
    val groups: List<TalkTopicGroup> = emptyList(),
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss
)
