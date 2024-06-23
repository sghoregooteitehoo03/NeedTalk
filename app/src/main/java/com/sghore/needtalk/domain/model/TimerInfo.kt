package com.sghore.needtalk.domain.model

data class TimerInfo(
    val participantInfoList: List<ParticipantInfo>,
    val timerTime: Long,
    val isAllowMic: Boolean,
    val maxMember: Int,
    val hostEndpointId: String = "",
    val isStart: Boolean = false
)