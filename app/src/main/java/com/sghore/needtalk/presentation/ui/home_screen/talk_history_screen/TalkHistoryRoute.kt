package com.sghore.needtalk.presentation.ui.home_screen.talk_history_screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.TalkHistory

@Composable
fun TalkHistoryRoute(
    viewModel: TalkHistoryViewModel = hiltViewModel(),
    navigateToTalkHistoryDetail: (TalkHistory) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    Surface {
        TalkHistoryScreen(
            uiState = uiState,
            onClickTalkHistory = navigateToTalkHistoryDetail
        )
    }
}