package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.domain.usecase.DeleteTalkTopicUseCase
import com.sghore.needtalk.domain.usecase.GetAllTalkTopicGroupUseCase
import com.sghore.needtalk.domain.usecase.GetTalkTopicsUseCase
import com.sghore.needtalk.domain.usecase.SaveGroupSegmentUseCase
import com.sghore.needtalk.domain.usecase.InsertTalkTopicGroupUseCase
import com.sghore.needtalk.domain.usecase.SetFavoriteUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class TalkTopicsDetailViewModel @Inject constructor(
    private val getTalkTopicUseCase: GetTalkTopicsUseCase,
    private val setFavoriteUseCase: SetFavoriteUseCase,
    private val deleteTalkTopicUseCase: DeleteTalkTopicUseCase,
    private val getAllTalkTopicGroupUseCase: GetAllTalkTopicGroupUseCase,
    private val insertTalkTopicGroupUseCase: InsertTalkTopicGroupUseCase,
    private val saveGroupSegmentUseCase: SaveGroupSegmentUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(TalkTopicsDetailUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = TalkTopicsDetailUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<TalkTopicsDetailUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        started = SharingStarted.Eagerly
    )

    private var favoriteJob: Job? = null

    init {
        // 어떤 타입의 대화주제를 가져올지(카테고리, 인기, 그룹)에 대한 JSON
        val talkTopicsDetailTypeJson = savedStateHandle.get<String>("type")
        if (talkTopicsDetailTypeJson != null) {
            // JSON을 객체로
            val type = Json.decodeFromString(
                TalkTopicsDetailType.serializer(), talkTopicsDetailTypeJson
            )

            // 타입에 맞는 페이징 데이터를 받음
            val talkTopics = getPagingTalkTopics(
                orderType = _uiState.value.orderType,
                talkTopicsDetailType = type
            )

            _uiState.update {
                it.copy(
                    talkTopics = talkTopics,
                    talkTopicsDetailType = type
                )
            }
        }
    }

    // 정렬 기준
    fun selectOrderType(orderType: OrderType) {
        // 타입에 맞는 페이징 데이터를 받음
        val talkTopics = getPagingTalkTopics(
            orderType = orderType,
            talkTopicsDetailType = _uiState.value.talkTopicsDetailType
        )

        _uiState.update {
            it.copy(
                orderType = orderType,
                talkTopics = talkTopics
            )
        }
    }

    fun setOpenDialog(dialogScreen: DialogScreen) {
        _uiState.update {
            it.copy(dialogScreen = dialogScreen)
        }
    }

    // 좋아요 설정
    fun setFavorite(
        topicId: String,
        uid: String,
        isFavorite: Boolean
    ) {
        // TODO: fix: 좋아요 여러번 눌림 방지
        favoriteJob?.cancel()
        favoriteJob = viewModelScope.launch {
            // 서버에서 대화주제 좋아요 설정
            setFavoriteUseCase(
                talkTopicId = topicId,
                uid = uid,
                isFavorite = isFavorite,
                onUpdate = { favoriteCount ->
                    // 좋아요 상태 업데이트
                    // UI 상태 업데이트
                    _uiState.update { state ->
                        state.copy(
                            favoriteHistory = state.favoriteHistory.toMutableMap().apply {
                                this[topicId] = FavoriteCounts(isFavorite, favoriteCount)
                            })
                    }
                    favoriteJob = null
                }
            )
        }
    }

    // 대화주제 삭제
    fun removeTalkTopic(talkTopic: TalkTopic) = viewModelScope.launch {
        // TODO: 삭제 후 리스트 업데이트
        deleteTalkTopicUseCase(talkTopic)
    }

    // 대화주제 모음집을 모두 가져옴
    fun getAllTalkTopicGroups(talkTopicId: String) =
        getAllTalkTopicGroupUseCase(talkTopicId = talkTopicId)

    fun saveTalkTopicGroup(selectedGroup: Map<Int, Boolean>, topicId: String, isPublic: Boolean) =
        viewModelScope.launch {
            saveGroupSegmentUseCase(selectedGroup, topicId, isPublic)
        }

    // 대화주제 모음집 제작
    fun addGroup(groupName: String) = viewModelScope.launch {
        val createdGroup =
            TalkTopicGroup(id = null, name = groupName, createdTime = System.currentTimeMillis())
        insertTalkTopicGroupUseCase(createdGroup)
    }


    // 대화주제 페이징 하여 가져옴(타입에 따라)
    private fun getPagingTalkTopics(
        orderType: OrderType,
        talkTopicsDetailType: TalkTopicsDetailType?
    ) = getTalkTopicUseCase(
        talkTopicsDetailType = talkTopicsDetailType,
        orderType = orderType,
        pageSize = 10
    )?.cachedIn(viewModelScope)

    fun handelEvent(event: TalkTopicsDetailUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}