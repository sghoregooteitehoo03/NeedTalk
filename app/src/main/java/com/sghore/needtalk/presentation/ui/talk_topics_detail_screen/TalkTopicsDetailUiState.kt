package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.paging.PagingData
import com.sghore.needtalk.domain.model.TalkTopic
import kotlinx.coroutines.flow.Flow

data class TalkTopicsDetailUiState(
    val talkTopics: PagingData<Flow<TalkTopic>>? = null,
    val orderType: OrderType = OrderType.Popular
)

sealed interface OrderType {
    data object Recently : OrderType

    data object Popular : OrderType
}