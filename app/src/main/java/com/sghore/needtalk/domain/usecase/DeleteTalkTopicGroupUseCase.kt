package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TalkTopicGroupEntity
import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopicGroup
import javax.inject.Inject

class DeleteTalkTopicGroupUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    suspend operator fun invoke(talkTopicGroup: TalkTopicGroup) {
        val deleteData = TalkTopicGroupEntity(
            id = talkTopicGroup.id,
            name = "",
            createdTime = 0,
            editedTime = 0
        )

        talkTopicRepository.deleteTalkTopicGroupEntity(deleteData)
    }
}