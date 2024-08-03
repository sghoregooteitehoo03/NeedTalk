package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TalkTopicGroupEntity
import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopicGroup
import javax.inject.Inject

class InsertTalkTopicGroupUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {

    suspend operator fun invoke(talkTopicGroup: TalkTopicGroup) {
        val insertData = TalkTopicGroupEntity(
            id = talkTopicGroup.id,
            name = talkTopicGroup.name,
            createdTime = talkTopicGroup.createdTime,
            editedTime = System.currentTimeMillis()
        )

        talkTopicRepository.insertTalkTopicGroupEntity(insertData)
    }
}