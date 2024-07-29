package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkTopicRepository
import javax.inject.Inject

class SetFavoriteUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository,
    private val crGroupSegmentUseCase: CRGroupSegmentUseCase
) {

    suspend operator fun invoke(
        talkTopicId: String,
        uid: String,
        isFavorite: Boolean
    ) {
        // 좋아요 한 대화모음집 추가
        crGroupSegmentUseCase(
            groupId = 1,
            topicId = talkTopicId,
            isPublic = true,
            isRemove = !isFavorite
        )

        // 좋아요 작업 수행
        talkTopicRepository.updateFavoriteCount(
            talkTopicId = talkTopicId,
            uid = uid,
            isFavorite = isFavorite
        )
    }
}