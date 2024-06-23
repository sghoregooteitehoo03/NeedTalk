package com.sghore.needtalk.presentation.ui.create_talk_screen

data class CreateTalkUiState(
    val talkTime: Long = 3600000L,
    val isTimer: Boolean = true,
    val isMicAllow: Boolean = true,
    val numberOfPeople: Int = 2,
    val isLoading: Boolean = true,
)
