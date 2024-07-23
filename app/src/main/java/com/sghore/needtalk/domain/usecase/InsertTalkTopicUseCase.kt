package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.document.TalkTopicDoc
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.util.generateTalkTopicId
import javax.inject.Inject

class InsertTalkTopicUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository,
    private val crGroupSegmentUseCase: CRGroupSegmentUseCase
) {
    suspend operator fun invoke(isPublic: Boolean, talkTopic: TalkTopic) {
        val currentTime = System.currentTimeMillis()
        val topicId = generateTalkTopicId(talkTopic.uid, currentTime)

        if (isPublic) {
            val createdTalkTopicDoc = TalkTopicDoc(
                id = topicId,
                uid = talkTopic.uid,
                topic = talkTopic.topic,
                categoryCode1 = talkTopic.category1.code,
                categoryCode2 = talkTopic.category2?.code ?: -1,
                categoryCode3 = talkTopic.category3?.code ?: -1,
                createdTime = currentTime
            )

            talkTopicRepository.insertTalkTopicDoc(createdTalkTopicDoc)
        } else {
            val talkTopicEntity = TalkTopicEntity(
                id = topicId,
                topic = talkTopic.topic,
                uid = talkTopic.uid,
                categoryCode1 = talkTopic.category1.code,
                categoryCode2 = talkTopic.category2?.code,
                categoryCode3 = talkTopic.category3?.code,
                createdTime = currentTime
            )

            talkTopicRepository.insertTalkTopicEntity(talkTopicEntity)
        }

        // 내가 제작한 대화주제 모음집에 추가
        crGroupSegmentUseCase(groupId = 0, topicId = topicId, isPublic = isPublic, isRemove = false)
    }
}