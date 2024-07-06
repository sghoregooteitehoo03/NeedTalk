package com.sghore.needtalk.presentation.ui.home_screen.talk_history_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.needtalk.data.repository.TalkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TalkHistoryViewModel @Inject constructor(
    private val talkRepository: TalkRepository
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(TalkHistoryUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        TalkHistoryUiState()
    )

    init {
        viewModelScope.launch {
            talkRepository.getTalkHistoryEntities(offset = 0, limit = 1)
                .collectLatest { // 대화기록이 업데이트 되었는지 확인
                    _uiState.update {
                        it.copy(
                            talkHistory = talkRepository.getPagingTalkHistory()
                                .cachedIn(viewModelScope)
                        )
                    }
                }
        }
    }
}