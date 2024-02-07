package com.sghore.needtalk.domain.model

data class TalkStatics(
    val totalTalkTime: Long,
    val dayOfWeekTalkTime: List<Long>,
    val participantCount: List<ParticipantCount>,
    val numberOfPeopleRate: List<Float>
)
