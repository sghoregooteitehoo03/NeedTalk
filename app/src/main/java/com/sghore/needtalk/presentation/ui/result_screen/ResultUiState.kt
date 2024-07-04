package com.sghore.needtalk.presentation.ui.result_screen

import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.getDefaultTalkTitle

data class ResultUiState(
    val talkTitle: String = getDefaultTalkTitle(),
    val otherUsers: List<UserData>
)
