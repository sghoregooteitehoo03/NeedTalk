package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.usecase.GetTalkTopicsUseCase2
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TalkTopicsDetailViewModel @Inject constructor(
    private val getTalkTopicUseCase: GetTalkTopicsUseCase2,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(TalkTopicsDetailUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TalkTopicsDetailUiState()
    )

    init {
        // 어떤 타입의 대화주제를 가져올지(카테고리, 인기, 그룹)에 대한 JSON
        val talkTopicsDetailTypeJson = savedStateHandle.get<String>("type")
        if (talkTopicsDetailTypeJson != null) {
            // JSON을 객체로
            val type =
                Json.decodeFromString(TalkTopicsDetailType.serializer(), talkTopicsDetailTypeJson)

            // 타입에 맞는 페이징 데이터를 받음
            val talkTopics = when (type) {
                is TalkTopicsDetailType.CategoryType -> {
                    getTalkTopicUseCase(
                        talkTopicsDetailType = type,
                        orderType = OrderType.Popular,
                        pageSize = 10
                    )
                }

                is TalkTopicsDetailType.GroupType -> null
                is TalkTopicsDetailType.PopularType -> null
            }?.cachedIn(viewModelScope)

            _uiState.update {
                it.copy(talkTopics = talkTopics)
            }
        }
    }
}