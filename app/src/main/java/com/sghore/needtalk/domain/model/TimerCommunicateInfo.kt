package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimerCommunicateInfo(
    val participantInfoList: List<ParticipantInfo?>,
    val currentTime: Long,
    val maxTime: Long,
    val isStopWatch: Boolean,
    val maxMember: Int,
    val talkTopics: List<TalkTopicEntity>,
    val timerActionState: TimerActionState
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
    @SerialName("TimerStop")
    data object TimerStop : TimerActionState

    @Serializable
    @SerialName("TimerRunning")
    data object TimerRunning : TimerActionState

    @Serializable
    @SerialName("StopWatchStop")
    data class StopWatchStop(val isFinished: Boolean) : TimerActionState

    @Serializable
    @SerialName("StopWatchRunning")
    data object StopWatchRunning : TimerActionState

    @Serializable
    @SerialName("TimerFinished")
    data object TimerFinished : TimerActionState
}
