package com.sghore.needtalk.domain.usecase

import javax.inject.Inject

class SaveGroupSegmentUseCase @Inject constructor(
    private val crGroupSegmentUseCase: CRGroupSegmentUseCase
) {
    suspend operator fun invoke(
        selectedGroup: Map<Int, Boolean>,
        topicId: String,
        isPublic: Boolean
    ) {
        selectedGroup.keys.forEach {
            if (selectedGroup.getOrDefault(it, false)) { // 선택이 되었다면 추가
                crGroupSegmentUseCase(
                    groupId = it,
                    topicId = topicId,
                    isPublic = isPublic,
                    isRemove = false
                )
            } else { // 선택이 해제되었다면 삭제
                crGroupSegmentUseCase(
                    groupId = it,
                    topicId = topicId,
                    isPublic = isPublic,
                    isRemove = true
                )
            }
        }
    }
}