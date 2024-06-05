package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.util.getCodeToCategory
import javax.inject.Inject

class GetPopularTalkTopicsUseCase @Inject constructor(
    private val talkTopicRepository: TalkTopicRepository
) {
    suspend operator fun invoke(limit: Long = 5): List<TalkTopic> {
        val talkTopicDocuments = talkTopicRepository.getPopularTalkTopics(limit)
        return talkTopicDocuments.map {
            TalkTopic(
                topicId = it.id,
                uid = it.uid,
                topic = it.topic,
                favoriteCount = it.favoriteCount,
                favorites = it.favorites,
                isUpload = it.isUpload,
                category1 = getCodeToCategory(it.categoryCode1)!!,
                category2 = getCodeToCategory(it.categoryCode2),
                category3 = getCodeToCategory(it.categoryCode3)
            )
        }
    }
}