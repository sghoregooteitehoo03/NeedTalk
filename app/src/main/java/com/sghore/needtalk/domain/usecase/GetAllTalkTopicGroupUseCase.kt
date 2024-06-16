package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopicGroup
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetAllTalkTopicGroupUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    operator fun invoke(talkTopicId: String) =
        talkTopicRepository.getAllTalkTopicGroupEntities()
            .map { listData ->
                val groupSegmentEntities = talkTopicRepository.getGroupSegmentEntities(talkTopicId)
                listData.map { talkTopicGroupEntity ->
                    TalkTopicGroup(
                        id = talkTopicGroupEntity.id,
                        name = talkTopicGroupEntity.name,
                        createdTime = talkTopicGroupEntity.createdTime,
                        isIncludeTopic = groupSegmentEntities.any {
                            (talkTopicGroupEntity.id ?: 0) == it.groupId
                        }
                    )
                }
            }
}