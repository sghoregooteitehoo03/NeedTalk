package com.sghore.needtalk.presentation.ui.timer_screen

import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TimerInfo

data class TimerUiState(
    val userEntity: UserEntity? = null,
    val timerInfo: TimerInfo? = null,
    val currentTime: Long = 0L,
    val timerActionState: TimerActionState = TimerActionState.TimerWaiting
)

sealed interface TimerActionState {
    data object TimerWaiting : TimerActionState
    data object TimerStop : TimerActionState
    data object TimerRunning : TimerActionState
}