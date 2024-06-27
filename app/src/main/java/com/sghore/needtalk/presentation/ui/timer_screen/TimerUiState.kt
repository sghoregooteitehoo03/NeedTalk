package com.sghore.needtalk.presentation.ui.timer_screen

import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.presentation.ui.DialogScreen

data class TimerUiState(
    val timerCommunicateInfo: TimerCommunicateInfo = TimerCommunicateInfo(),
    val hostEndpointId: String = "",
    val talkTopic: String = "",
    val isFlip: Boolean = false, // 한번만 동작되게 하기 위한 변수
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss,
)