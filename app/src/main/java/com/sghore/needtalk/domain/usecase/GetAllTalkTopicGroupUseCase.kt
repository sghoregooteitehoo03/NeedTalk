package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopicGroup
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllTalkTopicGroupUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    suspend operator fun invoke() =
        talkTopicRepository.getAllTalkTopicGroupEntity()
            .map {
                it.map { talkTopicGroupEntity ->
                    TalkTopicGroup(
                        id = talkTopicGroupEntity.id,
                        name = talkTopicGroupEntity.name,
                        createdTime = talkTopicGroupEntity.createdTime
                    )
                }
            }.first()
}