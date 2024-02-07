package com.sghore.needtalk.presentation.ui.statics_screen

import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TalkStatics

data class StaticsUiState(
    val userEntity: UserEntity? = null,
    val talkStatics: TalkStatics? = null,
    val baseDate: Long = -1L,
    val isLoading: Boolean = true
)
