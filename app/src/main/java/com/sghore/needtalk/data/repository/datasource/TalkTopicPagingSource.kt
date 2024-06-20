package com.sghore.needtalk.data.repository.datasource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.toObjects
import com.sghore.needtalk.data.model.document.TalkTopicDoc
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.presentation.ui.talk_topics_detail_screen.OrderType
import com.sghore.needtalk.util.Constants
import com.sghore.needtalk.util.getCodeToCategory
import kotlinx.coroutines.tasks.await

// TODO: .fix: 정렬 기능 동작하지 않음
class TalkTopicPagingSource(
    private val userId: String,
    private val firestore: FirebaseFirestore,
    private val talkTopicCategoryCode: Int,
    private val orderType: OrderType,
    private val limit: Int
) :
    PagingSource<QuerySnapshot, TalkTopic>() {
    override fun getRefreshKey(state: PagingState<QuerySnapshot, TalkTopic>): QuerySnapshot? {
        return null
    }

    override suspend fun load(params: LoadParams<QuerySnapshot>): LoadResult<QuerySnapshot, TalkTopic> {
        return try {
            val orderField = when (orderType) { // 정렬 타입
                OrderType.Popular -> "favoriteCount"
                OrderType.Recently -> "createdTime"
            }

            // 데이터를 읽어
            val currentPage = params.key ?: if (talkTopicCategoryCode != -1) {
                firestore.collection(Constants.COLLECTION_TALK_TOPIC)
                    .whereEqualTo("uploaded", true)
                    .where(
                        Filter.or(
                            Filter.equalTo("categoryCode1", talkTopicCategoryCode),
                            Filter.equalTo("categoryCode2", talkTopicCategoryCode),
                            Filter.equalTo("categoryCode3", talkTopicCategoryCode)
                        )
                    )
                    .orderBy(orderField, Query.Direction.DESCENDING)
                    .limit(limit.toLong())
                    .get()
                    .await()
            } else {
                firestore.collection(Constants.COLLECTION_TALK_TOPIC)
                    .whereEqualTo("uploaded", true)
                    .orderBy(orderField, Query.Direction.DESCENDING)
                    .limit(limit.toLong())
                    .get()
                    .await()
            }
            val lastDocSnapshot = currentPage.documents[currentPage.size() - 1]
            val nextPage = if (talkTopicCategoryCode != -1) {
                firestore.collection(Constants.COLLECTION_TALK_TOPIC)
                    .whereEqualTo("uploaded", true)
                    .where(
                        Filter.or(
                            Filter.equalTo("categoryCode1", talkTopicCategoryCode),
                            Filter.equalTo("categoryCode2", talkTopicCategoryCode),
                            Filter.equalTo("categoryCode3", talkTopicCategoryCode)
                        )
                    )
                    .orderBy(orderField, Query.Direction.DESCENDING)
                    .startAfter(lastDocSnapshot)
                    .limit(limit.toLong())
                    .get()
                    .await()
            } else {
                firestore.collection(Constants.COLLECTION_TALK_TOPIC)
                    .whereEqualTo("uploaded", true)
                    .orderBy(orderField, Query.Direction.DESCENDING)
                    .startAfter(lastDocSnapshot)
                    .limit(limit.toLong())
                    .get()
                    .await()
            }


            // mapping
            val talkTopics = currentPage.toObjects<TalkTopicDoc>().map {
                TalkTopic(
                    topicId = it.id,
                    uid = it.uid,
                    topic = it.topic,
                    favoriteCount = it.favorites.filter { favorites -> favorites.value }.size,
                    isFavorite = it.favorites.getOrDefault(userId, false),
                    isUpload = it.uploaded,
                    isPublic = true,
                    category1 = getCodeToCategory(it.categoryCode1)!!,
                    category2 = getCodeToCategory(it.categoryCode2),
                    category3 = getCodeToCategory(it.categoryCode3)
                )
            }

            LoadResult.Page(
                data = talkTopics,
                prevKey = null,
                nextKey = nextPage
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}