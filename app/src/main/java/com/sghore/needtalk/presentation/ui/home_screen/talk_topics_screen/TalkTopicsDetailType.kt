package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed interface TalkTopicsDetailType {
    @Serializable
    @SerialName("CategoryType")
    data class CategoryType(val categoryCode: Int, val userId: String, val title: String) :
        TalkTopicsDetailType

    @Serializable
    @SerialName("PopularType")
    data class PopularType(val index: Int) : TalkTopicsDetailType

    @Serializable
    @SerialName("GroupType")
    data class GroupType(val _code: Int) : TalkTopicsDetailType
}