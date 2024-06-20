package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopic
import javax.inject.Inject

class DeleteTalkTopicUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {

    suspend operator fun invoke(talkTopic: TalkTopic) {
        if (talkTopic.isPublic) {
            talkTopicRepository.deleteTalkTopicDoc(talkTopicId = talkTopic.topicId)
        } else {
            talkTopicRepository.deleteTalkTopicEntity(talkTopicId = talkTopic.topicId)
        }
    }
}