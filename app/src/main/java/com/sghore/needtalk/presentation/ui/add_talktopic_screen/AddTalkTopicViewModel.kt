package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.TalkTopicCategory
import com.sghore.needtalk.domain.usecase.InsertTalkTopicUseCase2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTalkTopicViewModel @Inject constructor(
    private val insertTalkTopicUseCase: InsertTalkTopicUseCase2,
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(AddTalkTopicUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = AddTalkTopicUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<AddTalkTopicUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    // 텍스트 변경
    fun changeText(text: String) {
        _uiState.update {
            it.copy(
                talkTopicText = text,
                isEnabled = text.isNotEmpty() && it.selectedCategories.isNotEmpty()
            )
        }
    }

    // 공개 여부 설정
    fun setPublic() {
        _uiState.update { it.copy(isPublic = !it.isPublic) }
    }

    // 카테고리 선택
    fun selectCategory(talkTopicCategory: TalkTopicCategory) {
        val selectedCategories = _uiState.value.selectedCategories.toMutableList()
        if (selectedCategories.contains(talkTopicCategory)) { // 선택이 되어 있으면 삭제
            selectedCategories.remove(talkTopicCategory)
        } else if (selectedCategories.size < 3) { // 선택이 되지 않았으며 3개 이하로 선택하였을 경우
            selectedCategories.add(talkTopicCategory)
        }

        _uiState.update {
            it.copy(
                selectedCategories = selectedCategories,
                isEnabled = it.talkTopicText.isNotEmpty() && selectedCategories.isNotEmpty()
            )
        }
    }

    fun insertTalkTopic(talkTopic: TalkTopic) = viewModelScope.launch {
        _uiState.update {
            it.copy(isLoading = true)
        }

        try {
            insertTalkTopicUseCase(isPublic = talkTopic.isPublic, talkTopic = talkTopic)
            handelEvent(AddTalkTopicUiEvent.SuccessAddTalkTopic)
        } catch (e: Exception) {
            e.printStackTrace()
            handelEvent(AddTalkTopicUiEvent.FailAddTalkTopic("오류가 발생했습니다."))
        }

        _uiState.update {
            it.copy(isLoading = false)
        }
    }

    fun handelEvent(event: AddTalkTopicUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}