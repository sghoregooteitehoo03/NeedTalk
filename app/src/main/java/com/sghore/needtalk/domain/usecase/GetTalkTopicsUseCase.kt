package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkRepository
import javax.inject.Inject

class GetTalkTopicsUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    operator fun invoke(groupCode: Int) = talkRepository.getTalkTopicEntity(groupCode)
}