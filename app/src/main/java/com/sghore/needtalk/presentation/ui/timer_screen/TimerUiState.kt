package com.sghore.needtalk.presentation.ui.timer_screen

import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TimerCommunicateInfo

data class TimerUiState(
    val userEntity: UserEntity? = null,
    val timerCommunicateInfo: TimerCommunicateInfo? = null
)