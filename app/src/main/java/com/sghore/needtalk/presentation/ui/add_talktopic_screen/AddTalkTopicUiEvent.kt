package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import com.sghore.needtalk.domain.model.TalkTopicCategory

sealed interface AddTalkTopicUiEvent {

    data object ClickNavigateBack : AddTalkTopicUiEvent

    data object ClickAddTalkTopic : AddTalkTopicUiEvent

    data class ChangeTalkTopicText(val text: String) : AddTalkTopicUiEvent

    data class ClickTalkTopicCategory(val category: TalkTopicCategory) : AddTalkTopicUiEvent

    data object ClickSetPublic : AddTalkTopicUiEvent
}
