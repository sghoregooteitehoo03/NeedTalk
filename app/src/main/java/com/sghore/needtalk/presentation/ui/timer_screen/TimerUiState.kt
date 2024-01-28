package com.sghore.needtalk.presentation.ui.timer_screen

import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.presentation.ui.DialogScreen

data class TimerUiState(
    val userEntity: UserEntity? = null,
    val timerCommunicateInfo: TimerCommunicateInfo? = null,
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss,
    val hostEndpointId: String = ""
)