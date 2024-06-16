package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.GroupSegmentEntity
import com.sghore.needtalk.data.repository.TalkTopicRepository
import javax.inject.Inject

class SaveGroupSegmentUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    suspend operator fun invoke(
        selectedGroup: Map<Int, Boolean>,
        topicId: String,
        isPublic: Boolean
    ) {
        selectedGroup.keys.forEach {
            if (selectedGroup.getOrDefault(it, false)) { // 선택이 되었다면 추가
                talkTopicRepository.insertGroupSegmentEntity(
                    GroupSegmentEntity(
                        groupId = it,
                        topicId = topicId,
                        isPublic = isPublic
                    )
                )
            } else { // 선택이 해제되었다면 삭제
                talkTopicRepository.deleteGroupSegmentEntity(it, topicId)
            }
        }
    }
}