package com.sghore.needtalk.presentation.ui.statics_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.usecase.GetTalkStaticsUseCase
import com.sghore.needtalk.util.getFirstTime
import com.sghore.needtalk.util.getLastTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class StaticsViewModel @Inject constructor(
    private val getTalkStaticsUseCase: GetTalkStaticsUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(StaticsUiState())
    private val _uiEvent = MutableSharedFlow<StaticsUiEvent>()

    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        StaticsUiState()
    )
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    init {
        val userEntityJson = savedStateHandle.get<String>("userEntity")

        if (userEntityJson != null) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)
            val baseDate = getFirstTime(System.currentTimeMillis())

            _uiState.update {// UI 업데이트
                it.copy(
                    userEntity = userEntity.copy(createTime = 1706670212000), // TODO: 테스트 값 나중에 지우기
                    baseDate = baseDate
                )
            }

            changeTimeRange(
                startTime = baseDate,
                endTime = getLastTime(baseDate)
            )
        }
    }

    fun changeTimeRange(
        startTime: Long,
        endTime: Long
    ) = viewModelScope.launch {
        val userEntity = _uiState.value.userEntity

        if (userEntity != null) {
            val statics = getTalkStaticsUseCase(
                currentUser = userEntity,
                startTime = startTime,
                endTime = endTime
            )

            _uiState.update {
                it.copy(
                    baseDate = startTime,
                    talkStatics = statics,
                    isLoading = false
                )
            }
        }
    }

    fun handelEvent(event: StaticsUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}