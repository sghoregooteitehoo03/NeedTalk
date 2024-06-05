package com.sghore.needtalk.data.model.document

data class TalkTopicDoc(
    val id: String = "",
    val uid: String = "",
    val topic: String = "",
    val favorites: HashMap<String, Boolean> = hashMapOf(),
    val favoriteCount: Int = 0,
    val isUpload: Boolean = false,
    val categoryCode1: Int = -1,
    val categoryCode2: Int = -1,
    val categoryCode3: Int = -1,
    val createdTime: Long = System.currentTimeMillis()
)
