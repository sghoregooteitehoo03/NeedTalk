package com.sghore.needtalk.presentation.ui.create_talk_screen

import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.presentation.ui.DialogScreen

data class CreateTalkUiState(
    val talkTime: Long = 3600000L,
    val isTimer: Boolean = true,
    val isMicAllow: Boolean = true,
    val numberOfPeople: Int = 2,
    val isLoading: Boolean = true,
)
