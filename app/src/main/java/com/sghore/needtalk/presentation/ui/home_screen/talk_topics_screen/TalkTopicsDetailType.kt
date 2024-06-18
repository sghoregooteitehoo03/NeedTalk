package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class TalkTopicsDetailType(val title: String) {
    @Serializable
    @SerialName("CategoryType")
    data class CategoryType(val categoryCode: Int, val userId: String, val _title: String) :
        TalkTopicsDetailType(_title)

    @Serializable
    @SerialName("PopularType")
    data class PopularType(val index: Int, val userId: String, val _title: String) :
        TalkTopicsDetailType(_title)

    @Serializable
    @SerialName("GroupType")
    data class GroupType(
        val code: Int,
        val _title: String
    ) : TalkTopicsDetailType(_title)
}