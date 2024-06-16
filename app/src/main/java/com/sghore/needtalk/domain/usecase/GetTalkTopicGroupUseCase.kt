package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TalkTopicGroupEntity
import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopicGroup
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class GetTalkTopicGroupUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    operator fun invoke() = talkTopicRepository.getTalkTopicGroupEntities(0)
        .map {
            it.map { talkTopicGroupEntity ->
                TalkTopicGroup(
                    id = talkTopicGroupEntity.id,
                    name = talkTopicGroupEntity.name,
                    createdTime = talkTopicGroupEntity.createdTime,
                )
            }
        }.onEach {
            if (it.isEmpty()) { // 초기 데이터 세팅
                val currentTime = System.currentTimeMillis()
                talkTopicRepository.insertTalkTopicGroupEntity(
                    TalkTopicGroupEntity(
                        id = 0,
                        name = "제작한 대화주제",
                        createdTime = currentTime,
                        editedTime = currentTime
                    )
                )
                talkTopicRepository.insertTalkTopicGroupEntity(
                    TalkTopicGroupEntity(
                        id = 1,
                        name = "좋아요 한 대화주제",
                        createdTime = currentTime,
                        editedTime = currentTime
                    )
                )
            }
        }
}