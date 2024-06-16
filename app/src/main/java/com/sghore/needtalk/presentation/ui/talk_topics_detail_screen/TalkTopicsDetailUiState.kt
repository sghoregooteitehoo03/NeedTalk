package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.paging.PagingData
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import kotlinx.coroutines.flow.Flow

data class TalkTopicsDetailUiState(
    val talkTopics: Flow<PagingData<TalkTopic>>? = null,
    val orderType: OrderType = OrderType.Popular,
    val talkTopicsDetailType: TalkTopicsDetailType? = null,
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss
)

sealed interface OrderType {
    data object Recently : OrderType

    data object Popular : OrderType
}