package com.sghore.needtalk.presentation.ui.home_screen

import com.sghore.needtalk.data.model.UserEntity

data class HomeUiState(
    val user: UserEntity? = null,
    val isStart: Boolean = false
)
