package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.repository.TalkRepository
import javax.inject.Inject

class InsertTalkTopicUseCase @Inject constructor(private val talkRepository: TalkRepository) {
    suspend operator fun invoke(talkTopicEntity: TalkTopicEntity) =
        talkRepository.insertTalkTopic(talkTopicEntity)
}