package com.sghore.needtalk.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimerCommunicateInfo(
    val participantInfoList: List<ParticipantInfo?> = listOf(),
    val currentTime: Long = -1L,
    val maxTime: Long = -1L,
    val isTimer: Boolean = false,
    val isAllowMic: Boolean = false,
    val maxMember: Int = -1,
    val pinnedTalkTopic: PinnedTalkTopic? = null,
    val timerActionState: TimerActionState = TimerActionState.TimerWaiting
)

@Serializable
sealed interface TimerActionState {
    @Serializable
    @SerialName("TimerWaiting")
    data object TimerWaiting : TimerActionState

    @Serializable
    @SerialName("TimerReady")
    data object TimerReady : TimerActionState

    @Serializable
    @SerialName("TimerPause")
    data object TimerPause : TimerActionState

    @Serializable
    @SerialName("TimerRunning")
    data object TimerRunning : TimerActionState

    @Serializable
    @SerialName("StopWatchPause")
    data class StopWatchPause(val isFinished: Boolean) : TimerActionState

    @Serializable
    @SerialName("StopWatchRunning")
    data object StopWatchRunning : TimerActionState

    @Serializable
    @SerialName("TimerFinished")
    data object TimerFinished : TimerActionState

    @Serializable
    @SerialName("TimerError")
    data class TimerError(val errorMsg: String) : TimerActionState
}
