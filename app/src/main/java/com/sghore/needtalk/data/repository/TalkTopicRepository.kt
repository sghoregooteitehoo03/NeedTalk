package com.sghore.needtalk.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.sghore.needtalk.data.model.document.TalkTopicDoc
import com.sghore.needtalk.data.model.entity.GroupSegmentEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity2
import com.sghore.needtalk.data.model.entity.TalkTopicGroupEntity
import com.sghore.needtalk.data.repository.database.TalkTopicDao
import com.sghore.needtalk.data.repository.datasource.SavedTalkTopicPagingSource
import com.sghore.needtalk.data.repository.datasource.TalkTopicPagingSource
import com.sghore.needtalk.presentation.ui.talk_topics_detail_screen.OrderType
import com.sghore.needtalk.util.Constants
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject

class TalkTopicRepository @Inject constructor(
    private val talkTopicDao: TalkTopicDao,
    private val firestore: FirebaseFirestore
) {

    // ID에 해당하는 대화주제 모음집을 가져옴
    suspend fun getTalkTopicGroupEntity(groupId: Int) =
        talkTopicDao.getTalkTopicGroupEntity(groupId = groupId)

    // 대화주제 모음집을 가져옴
    fun getTalkTopicGroupEntities(offset: Int = 0, limit: Int = 5) =
        talkTopicDao.getTalkTopicGroupEntities(offset, limit)

    // 대화주제 모음집을 모두 가져옴
    fun getAllTalkTopicGroupEntities() = talkTopicDao.getAllTalkTopicGroupEntities()

    // 대화주제 모음집 추가
    suspend fun insertTalkTopicGroupEntity(groupEntity: TalkTopicGroupEntity) =
        talkTopicDao.insertTalkTopicGroupEntity(groupEntity)

    suspend fun deleteTalkTopicGroupEntity(groupEntity: TalkTopicGroupEntity) =
        talkTopicDao.deleteTalkTopicGroupEntity(groupEntity)


    // 모음집 조각 데이터 가져옴
    suspend fun getGroupSegmentEntities(topicId: String) =
        talkTopicDao.getGroupSegmentEntities(topicId)

    // 모음집 조각 데이터 추가
    suspend fun insertGroupSegmentEntity(groupSegmentEntity: GroupSegmentEntity) =
        talkTopicDao.insertGroupSegmentEntity(groupSegmentEntity)

    // 모음집 조각 데이터 삭제
    suspend fun deleteGroupSegmentEntity(groupId: Int, talkTopicId: String) =
        talkTopicDao.deleteGroupSegmentEntity(groupId, talkTopicId)


    // 비공개 대화주제 추가
    suspend fun insertTalkTopicEntity(talkTopicEntity: TalkTopicEntity2) =
        talkTopicDao.insertTalkTopicEntity(talkTopicEntity)

    // 공개 대화주제 추가
    suspend fun insertTalkTopicDoc(talkTopicDoc: TalkTopicDoc) {
        firestore.collection(Constants.COLLECTION_TALK_TOPIC)
            .document(talkTopicDoc.id)
            .set(talkTopicDoc)
            .await()
    }

    // 인기 대화주제를 firestore에서 가져옵니다.
    suspend fun getPopularTalkTopics(limit: Long) =
        firestore.collection(Constants.COLLECTION_TALK_TOPIC)
            .orderBy("favoriteCount", Query.Direction.DESCENDING)
            .whereEqualTo("uploaded", true)
            .limit(limit)
            .get()
            .await()
            .toObjects<TalkTopicDoc>()

    // 대화주제를 페이징 하여 가져옴
    fun getPagingTalkTopics(
        userId: String,
        talkTopicCategoryCode: Int,
        orderType: OrderType,
        pageSize: Int
    ) =
        Pager(PagingConfig(pageSize = pageSize)) {
            TalkTopicPagingSource(
                userId = userId,
                firestore = firestore,
                talkTopicCategoryCode = talkTopicCategoryCode,
                orderType = orderType,
                limit = pageSize
            )
        }.flow

    // 모음집에 저장된 대화주제를 페이징 하여 가져옴
    fun getSavedTalkTopics(
        userId: String,
        groupId: Int,
        pageSize: Int
    ) = Pager(PagingConfig(pageSize = pageSize)) {
        SavedTalkTopicPagingSource(
            userId = userId,
            groupId = groupId,
            pageSize = pageSize,
            firestore = firestore,
            talkTopicDao = talkTopicDao
        )
    }.flow

    // 대화주제 좋아요 표시
    suspend fun updateFavoriteCount(
        talkTopicId: String,
        uid: String,
        isFavorite: Boolean,
        onUpdate: (favoriteCount: Int) -> Unit
    ) {
        val ref = firestore.collection(Constants.COLLECTION_TALK_TOPIC)
            .document(talkTopicId)

        firestore.runTransaction { transaction ->
            val talkTopicDoc = transaction.get(ref).toObject(TalkTopicDoc::class.java)

            if (isFavorite) { // 좋아요 설정 시
                val updateFavoriteCount = (talkTopicDoc?.favoriteCount ?: 0) + 1
                talkTopicDoc?.favorites?.set(uid, true)

                transaction.update(
                    ref,
                    mapOf(
                        "favoriteCount" to updateFavoriteCount,
                        "favorites" to talkTopicDoc?.favorites
                    )
                )
                onUpdate(updateFavoriteCount)
            } else { // 좋아요 취소 시
                val updateFavoriteCount = (talkTopicDoc?.favoriteCount ?: 0) - 1
                talkTopicDoc?.favorites?.set(uid, false)

                transaction.update(
                    ref,
                    mapOf(
                        "favoriteCount" to updateFavoriteCount,
                        "favorites" to talkTopicDoc?.favorites
                    )
                )
                onUpdate(updateFavoriteCount)
            }
        }.await()
    }

    // 온라인 대화주제 삭제
    suspend fun deleteTalkTopicDoc(talkTopicId: String) {
        firestore.collection(Constants.COLLECTION_TALK_TOPIC)
            .document(talkTopicId)
            .delete()
            .await()
    }

    // 오프라인 대화주제 삭제
    suspend fun deleteTalkTopicEntity(talkTopicId: String) {
        talkTopicDao.deleteTalkTopicEntity(talkTopicId)
    }

    // TODO: 나중에 지울것
    fun setData() {
        val uid = ""
        val time = System.currentTimeMillis()

        val input = uid + time
        val bytes = MessageDigest
            .getInstance("SHA-256")
            .digest(input.toByteArray())
        val id = bytes.joinToString("") { "%02x".format(it) }

        val data = TalkTopicDoc(
            id = id,
            uid = "",
            topic = "인생에 대한 목표가 무엇인가요?",
            uploaded = true,
            categoryCode1 = 5,
            categoryCode2 = -1,
            categoryCode3 = -1,
            createdTime = time
        )

        firestore.collection(Constants.COLLECTION_TALK_TOPIC)
            .document(id)
            .set(data)
            .addOnSuccessListener {
                Log.i("Check", "Success")
            }
            .addOnFailureListener {
                Log.i("Check", "Fail: ${it.printStackTrace()}")
            }
    }
}