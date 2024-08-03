package com.sghore.needtalk.presentation.ui.result_screen

import com.sghore.needtalk.domain.model.TalkResult
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.domain.model.UserTalkResult
import com.sghore.needtalk.util.getDefaultTalkTitle

data class ResultUiState(
    val talkTitle: String = getDefaultTalkTitle(),
    val fileSize: Long = 0L,
    val otherUsers: List<UserData?> = listOf(),
    val talkResult: TalkResult? = null,
    val animationEndList: List<Boolean> = listOf(),
    val isLoading: Boolean = true
)
