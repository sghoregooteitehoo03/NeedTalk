package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import androidx.paging.map
import com.sghore.needtalk.data.repository.TalkTopicRepository
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.domain.usecase.GetAllTalkTopicGroupUseCase
import com.sghore.needtalk.domain.usecase.GetTalkTopicsUseCase2
import com.sghore.needtalk.domain.usecase.SaveGroupSegmentUseCase
import com.sghore.needtalk.domain.usecase.InsertTalkTopicGroupUseCase
import com.sghore.needtalk.domain.usecase.SetFavoriteUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val talkTopicRepository: TalkTopicRepository,
    private val getTalkTopicUseCase: GetTalkTopicsUseCase2,
    private val setFavoriteUseCase: SetFavoriteUseCase,
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

            // TODO: 툴바에 표시 될 타이틀을 이전 데이터에서 전달하여 해당 화면에 표시하게 구현
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
            orderType = _uiState.value.orderType,
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
    ) = viewModelScope.launch {

        // TODO: .fix: 좋아요 클릭 시 리스트 전체 업데이트가 되어버림
        // 서버에서 대화주제 좋아요 설정
        setFavoriteUseCase(
            talkTopicId = topicId,
            uid = uid,
            isFavorite = isFavorite,
            onUpdate = { favoriteCount ->
                // 리스트 데이터 업데이트
                val updateTalkTopics = _uiState.value.talkTopics?.map { pagingData ->
                    pagingData.map { talkTopic ->
                        if (talkTopic.topicId == topicId) {
                            talkTopic.copy(
                                favoriteCount = favoriteCount,
                                isFavorite = isFavorite
                            )
                        } else {
                            talkTopic
                        }
                    }
                }

                // UI 상태 업데이트
                _uiState.update {
                    it.copy(talkTopics = updateTalkTopics)
                }
            }
        )
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