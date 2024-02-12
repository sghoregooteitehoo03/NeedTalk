package com.sghore.needtalk.domain.model

import com.sghore.needtalk.data.model.entity.UserEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface PayloadType {
    @Serializable
    @SerialName("UpdateTimerCmInfo")
    data class UpdateTimerCmInfo(val timerCommunicateInfo: TimerCommunicateInfo) : PayloadType

    @Serializable
    @SerialName("ClientJoinTimer")
    data class ClientJoinTimer(val user: UserEntity) : PayloadType

    @Serializable
    @SerialName("ClientReady")
    data class ClientReady(val isReady: Boolean, val participantIndex: Int) : PayloadType

    @Serializable
    @SerialName("RejectJoin")
    data class RejectJoin(val rejectMessage: String) : PayloadType
}