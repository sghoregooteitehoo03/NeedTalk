package com.sghore.needtalk.data.repository.datasource

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.FirebaseFirestore
import com.sghore.needtalk.data.model.document.TalkTopicDoc
import com.sghore.needtalk.data.repository.database.TalkTopicDao
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.util.Constants
import com.sghore.needtalk.util.getCodeToCategory
import kotlinx.coroutines.tasks.await

class SavedTalkTopicPagingSource(
    private val userId: String,
    private val groupId: Int,
    private val pageSize: Int,
    private val talkTopicDao: TalkTopicDao,
    private val firestore: FirebaseFirestore
) : PagingSource<Int, TalkTopic>() {
    override fun getRefreshKey(state: PagingState<Int, TalkTopic>): Int? {
        return null
    }

    // TODO: 데이터 추가 후 다시 테스트 해보기
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, TalkTopic> {
        return try {
            val offset = params.key ?: 0
            val groupSegmentEntities =
                talkTopicDao.getGroupSegmentEntities(groupId, offset = offset, limit = pageSize)

            if (groupSegmentEntities.isEmpty())
                throw NullPointerException()

            val talkTopics = groupSegmentEntities.map {
                if (it.isPublic) {
                    val talkTopicDoc = firestore.collection(Constants.COLLECTION_TALK_TOPIC)
                        .document(it.topicId)
                        .get()
                        .await()
                        .toObject(TalkTopicDoc::class.java)!!

                    TalkTopic(
                        topicId = talkTopicDoc.id,
                        uid = talkTopicDoc.uid,
                        topic = talkTopicDoc.topic,
                        favoriteCount = talkTopicDoc.favoriteCount,
                        isFavorite = talkTopicDoc.favorites.getOrDefault(userId, false),
                        isUpload = talkTopicDoc.uploaded,
                        isPublic = true,
                        category1 = getCodeToCategory(talkTopicDoc.categoryCode1)!!,
                        category2 = getCodeToCategory(talkTopicDoc.categoryCode2),
                        category3 = getCodeToCategory(talkTopicDoc.categoryCode3),
                    )
                } else {
                    val talkTopicEntity = talkTopicDao.getTalkTopicEntity(talkTopicId = it.topicId)

                    TalkTopic(
                        topicId = talkTopicEntity.id,
                        uid = talkTopicEntity.uid,
                        topic = talkTopicEntity.topic,
                        favoriteCount = 0,
                        isFavorite = false,
                        isUpload = false,
                        isPublic = false,
                        category1 = getCodeToCategory(talkTopicEntity.categoryCode1)!!,
                        category2 = getCodeToCategory(talkTopicEntity.categoryCode2 ?: -1),
                        category3 = getCodeToCategory(talkTopicEntity.categoryCode3 ?: -1),
                    )
                }
            }

            LoadResult.Page(
                talkTopics,
                prevKey = null,
                nextKey = offset + pageSize
            )
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }
}