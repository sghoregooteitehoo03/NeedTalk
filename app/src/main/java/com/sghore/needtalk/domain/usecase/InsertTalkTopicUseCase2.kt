package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.document.TalkTopicDoc
import com.sghore.needtalk.data.model.entity.TalkTopicEntity2
import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.util.generateTalkTopicId
import javax.inject.Inject

class InsertTalkTopicUseCase2 @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    suspend operator fun invoke(isPublic: Boolean, talkTopic: TalkTopic) {
        val currentTime = System.currentTimeMillis()

        if (isPublic) {
            val createdTalkTopicDoc = TalkTopicDoc(
                id = generateTalkTopicId(talkTopic.uid, currentTime),
                uid = talkTopic.uid,
                topic = talkTopic.topic,
                categoryCode1 = talkTopic.category1.code,
                categoryCode2 = talkTopic.category2?.code ?: -1,
                categoryCode3 = talkTopic.category3?.code ?: -1,
                createdTime = currentTime
            )

            talkTopicRepository.insertTalkTopicDoc(createdTalkTopicDoc)
        } else {
            val talkTopicEntity = TalkTopicEntity2(
                id = generateTalkTopicId(talkTopic.uid, currentTime),
                topic = talkTopic.topic,
                categoryCode1 = talkTopic.category1.code,
                categoryCode2 = talkTopic.category2?.code,
                categoryCode3 = talkTopic.category3?.code,
                createdTime = currentTime
            )

            talkTopicRepository.insertTalkTopicEntity(talkTopicEntity)
        }
    }
}