package com.sghore.needtalk.presentation.ui.create_talk_screen

import com.sghore.needtalk.data.model.entity.TalkTopicEntity


sealed interface CreateTalkUiEvent {
    data object ClickBackArrow : CreateTalkUiEvent
    data object ClickComplete : CreateTalkUiEvent

    data class ChangeTime(val talkTime: Long) : CreateTalkUiEvent

    data class ClickStopWatchMode(val isAllow: Boolean) : CreateTalkUiEvent

    data class ClickNumberOfPeople(val number: Int) : CreateTalkUiEvent

    data class ClickTopicCategory(val topicCategory: String, val groupCode: Int) : CreateTalkUiEvent

    data object ClickAddTopic : CreateTalkUiEvent

    data class ClickRemoveTopic(val talkTopicEntity: TalkTopicEntity) : CreateTalkUiEvent

    data class ErrorMessage(val message: String) : CreateTalkUiEvent

}