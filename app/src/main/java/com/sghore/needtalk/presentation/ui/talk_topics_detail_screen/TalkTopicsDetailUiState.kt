package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.compose.foundation.lazy.LazyListState
import androidx.paging.PagingData
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import kotlinx.coroutines.flow.Flow

data class TalkTopicsDetailUiState(
    val talkTopics: Flow<PagingData<TalkTopic>>? = null,
    val lazyListState: LazyListState? = null,
    val favoriteHistory: Map<String, FavoriteCounts> = mapOf(),
    val orderType: OrderType = OrderType.Popular,
    val talkTopicsDetailType: TalkTopicsDetailType? = null,
    val dialogScreen: DialogScreen = DialogScreen.DialogDismiss
)

data class FavoriteCounts(
    val isFavorite: Boolean,
    val count: Int
)

sealed interface OrderType {
    data object Recently : OrderType

    data object Popular : OrderType
}