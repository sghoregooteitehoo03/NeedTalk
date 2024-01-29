package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.MusicEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TimerCommunicateInfo(
    val participantInfoList: List<ParticipantInfo>,
    val musicInfo: MusicEntity,
    val currentTime: Long,
    val maxTime: Long,
    val maxMember: Int,
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
}
