package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.repository.TalkRepository
import javax.inject.Inject

class RemoveTalkTopicUseCase @Inject constructor(private val talkRepository: TalkRepository) {
    suspend operator fun invoke(talkTopicEntity: TalkTopicEntity) =
        talkRepository.deleteTalkTopic(talkTopicEntity)
}