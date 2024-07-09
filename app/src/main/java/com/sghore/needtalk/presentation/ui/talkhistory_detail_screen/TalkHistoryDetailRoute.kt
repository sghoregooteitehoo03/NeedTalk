package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TalkHistoryDetailRoute(
    viewModel: TalkHistoryDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    Surface {
        TalkHistoryDetailScreen(
            uiState = uiState
        )
    }
}