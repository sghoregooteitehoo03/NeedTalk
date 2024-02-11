package com.sghore.needtalk.presentation.ui.create_screen

import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.presentation.ui.DialogScreen

data class CreateUiState(
    val userEntity: UserEntity? = null,
    val isLoading: Boolean = true,
    val talkTime: Long = 3600000L,
    val isStopwatch: Boolean = false,
    val numberOfPeople: Int = 2,
    val talkTopics: List<TalkTopicEntity> = listOf(),
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss
)
