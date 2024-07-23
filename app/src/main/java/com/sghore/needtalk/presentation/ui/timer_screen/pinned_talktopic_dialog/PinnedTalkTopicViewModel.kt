package com.sghore.needtalk.presentation.ui.timer_screen.pinned_talktopic_dialog

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.domain.usecase.GetAllTalkTopicGroupUseCase
import com.sghore.needtalk.domain.usecase.GetTalkTopicsUseCase
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import com.sghore.needtalk.presentation.ui.talk_topics_detail_screen.OrderType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PinnedTalkTopicViewModel @Inject constructor(
    private val getAllTalkTopicGroupUseCase: GetAllTalkTopicGroupUseCase,
    private val getTalkTopicUseCase: GetTalkTopicsUseCase
) : ViewModel() {
    var page by mutableIntStateOf(1)
    var talkTopicGroups by mutableStateOf(listOf<TalkTopicGroup>())

    var talkTopics by mutableStateOf<Flow<PagingData<TalkTopic>>?>(null)
    var title by mutableStateOf("")

    init {
        viewModelScope.launch {
            talkTopicGroups = getAllTalkTopicGroupUseCase("").first()
        }
    }

    fun selectGroup(group: TalkTopicGroup, userId: String) {
        page = 2
        title = group.name

        viewModelScope.launch {
            talkTopics = getTalkTopicUseCase(
                talkTopicsDetailType = TalkTopicsDetailType.GroupType(
                    groupId = group.id ?: 0,
                    userId = userId,
                    _title = ""
                ),
                orderType = OrderType.Popular,
                pageSize = 10
            )?.cachedIn(viewModelScope)
        }
    }

    fun clearData() {
        page = 1
        title = ""
        talkTopics = null
    }
}