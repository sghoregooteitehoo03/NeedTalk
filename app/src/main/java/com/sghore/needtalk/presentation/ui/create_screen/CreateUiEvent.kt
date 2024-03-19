package com.sghore.needtalk.presentation.ui.create_screen

import com.sghore.needtalk.data.model.entity.TalkTopicEntity


sealed interface CreateUiEvent {
    data object ClickBackArrow : CreateUiEvent
    data object ClickComplete : CreateUiEvent

    data class ChangeTime(val talkTime: Long) : CreateUiEvent

    data class ClickStopWatchMode(val isAllow: Boolean) : CreateUiEvent

    data class ClickNumberOfPeople(val number: Int) : CreateUiEvent

    data class ClickTopicCategory(val topicCategory: String, val groupCode: Int) : CreateUiEvent

    data object ClickAddTopic : CreateUiEvent

    data class ClickRemoveTopic(val talkTopicEntity: TalkTopicEntity) : CreateUiEvent

    data class ErrorMessage(val message: String) : CreateUiEvent

}