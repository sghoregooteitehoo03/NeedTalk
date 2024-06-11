package com.sghore.needtalk.data.repository.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.sghore.needtalk.data.model.document.TalkTopicDoc
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.presentation.ui.talk_topics_detail_screen.OrderType
import com.sghore.needtalk.util.Constants
import com.sghore.needtalk.util.getCodeToCategory
import kotlinx.coroutines.tasks.await

class TalkTopicPagingSource(
    private val firestore: FirebaseFirestore,
    private val talkTopicCategoryCode: Int,
    private val orderType: OrderType,
    private val limit: Int
) :
    PagingSource<Int, TalkTopic>() {
    override fun getRefreshKey(state: PagingState<Int, TalkTopic>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TalkTopic> {
        return try {
            val offset = params.key ?: 0
            val orderField = when (orderType) { // 정렬 타입
                OrderType.Popular -> "favoriteCount"
                OrderType.Recently -> "createdTime"
            }

            // 데이터를 읽어
            val talkTopicDocs = firestore.collection(Constants.COLLECTION_TALK_TOPIC)
                .whereEqualTo("isUpload", true)
                .where(
                    Filter.or(
                        Filter.equalTo("categoryCode1", talkTopicCategoryCode),
                        Filter.equalTo("categoryCode2", talkTopicCategoryCode),
                        Filter.equalTo("categoryCode3", talkTopicCategoryCode)
                    )
                )
                .orderBy(orderField, Query.Direction.DESCENDING)
                .startAt(offset)
                .limit(limit.toLong())
                .get()
                .await()
                .toObjects<TalkTopicDoc>()

            // mapping
            val talkTopics = talkTopicDocs.map {
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

            LoadResult.Page(
                data = talkTopics,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}