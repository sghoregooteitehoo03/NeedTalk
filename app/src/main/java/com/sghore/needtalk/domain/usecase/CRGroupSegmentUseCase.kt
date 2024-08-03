package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.GroupSegmentEntity
import com.sghore.needtalk.data.repository.TalkTopicRepository
import javax.inject.Inject

class CRGroupSegmentUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    suspend operator fun invoke(
        groupId: Int,
        topicId: String,
        isPublic: Boolean,
        isRemove: Boolean
    ) {
        // 그룹의 정보를 가져옴
        val group = talkTopicRepository.getTalkTopicGroupEntity(groupId)
            ?.copy(editedTime = System.currentTimeMillis()) // 수정 시간 업데이트

        if (group != null) {
            val groupSegmentEntity = GroupSegmentEntity(
                groupId = groupId,
                topicId = topicId,
                isPublic
            )

            if (isRemove) { // 대화주제 모음집 조각 제거
                talkTopicRepository.deleteGroupSegmentEntity(groupId, topicId)
            } else { // 대화주제 모음집 조각 추가
                talkTopicRepository.insertGroupSegmentEntity(groupSegmentEntity)
            }

            // 대화주제 모음집 수정시간 업데이트
            talkTopicRepository.insertTalkTopicGroupEntity(group)
        }
    }
}