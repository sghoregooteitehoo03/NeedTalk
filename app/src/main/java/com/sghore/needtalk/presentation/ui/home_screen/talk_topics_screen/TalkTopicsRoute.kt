package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TalkTopicsRoute(
    viewModel: TalkTopicsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(
        viewModel.uiEvent
    ) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is TalkTopicsUiEvent.ClickAddTopic -> {

                }

                is TalkTopicsUiEvent.ClickGroupMore -> {

                }

                is TalkTopicsUiEvent.ClickPopularMore -> {

                }

                is TalkTopicsUiEvent.ClickTalkTopic -> {

                }

                is TalkTopicsUiEvent.ClickTopicCategory -> {

                }

                is TalkTopicsUiEvent.ClickGroup -> {

                }
            }
        }
    }

    TalkTopicsScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}