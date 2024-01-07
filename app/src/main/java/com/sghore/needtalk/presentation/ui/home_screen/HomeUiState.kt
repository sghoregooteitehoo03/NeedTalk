package com.sghore.needtalk.presentation.ui.home_screen

import androidx.paging.PagingData
import com.sghore.needtalk.data.model.UserEntity
import com.sghore.needtalk.domain.model.TalkHistory
import kotlinx.coroutines.flow.Flow

data class HomeUiState(
    val user: UserEntity? = null,
    val talkHistory: Flow<PagingData<TalkHistory>>? = null,
    val isStart: Boolean = false,
    val isDialogOpen: Boolean = false
)
