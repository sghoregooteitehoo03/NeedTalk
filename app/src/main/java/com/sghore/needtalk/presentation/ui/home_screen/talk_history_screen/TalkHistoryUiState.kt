package com.sghore.needtalk.presentation.ui.home_screen.talk_history_screen

import androidx.paging.PagingData
import com.sghore.needtalk.domain.model.TalkHistory
import kotlinx.coroutines.flow.Flow

data class TalkHistoryUiState(val talkHistory: Flow<PagingData<TalkHistory>>? = null)
