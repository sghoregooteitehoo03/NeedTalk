package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import com.sghore.needtalk.presentation.ui.talk_topics_detail_screen.OrderType
import javax.inject.Inject

class GetTalkTopicsUseCase2 @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    operator fun invoke(
        talkTopicsDetailType: TalkTopicsDetailType?,
        orderType: OrderType,
        pageSize: Int
    ) = when (talkTopicsDetailType) {
        is TalkTopicsDetailType.CategoryType -> { // 카테고리 타입의 대화주제
            talkTopicRepository.getPagingTalkTopics(
                userId = talkTopicsDetailType.userId,
                talkTopicCategoryCode = talkTopicsDetailType.categoryCode,
                orderType = orderType,
                pageSize = pageSize
            )
        }

        is TalkTopicsDetailType.PopularType -> {
            talkTopicRepository.getPagingTalkTopics(
                userId = talkTopicsDetailType.userId,
                talkTopicCategoryCode = -1,
                orderType = orderType,
                pageSize = pageSize
            )
        } // 인기 대화주제
        is TalkTopicsDetailType.GroupType -> null // 모음집 대화주제
        else -> null
    }
}