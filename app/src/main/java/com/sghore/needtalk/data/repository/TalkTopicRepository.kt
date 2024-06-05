package com.sghore.needtalk.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.toObjects
import com.sghore.needtalk.data.model.document.TalkTopicDoc
import com.sghore.needtalk.util.Constants
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import javax.inject.Inject

class TalkTopicRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun getPopularTalkTopics(limit: Long) =
        firestore.collection(Constants.COLLECTION_TALK_TOPIC)
            .orderBy("favoriteCount", Query.Direction.DESCENDING)
            .limit(limit)
            .get()
            .await()
            .toObjects<TalkTopicDoc>()

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
            isUpload = true,
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