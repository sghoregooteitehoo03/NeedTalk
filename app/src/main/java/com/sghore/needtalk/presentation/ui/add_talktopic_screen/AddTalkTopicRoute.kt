package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddTalkTopicRoute(
    viewModel: AddTalkTopicViewModel = hiltViewModel(),
    navigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is AddTalkTopicUiEvent.ChangeTalkTopicText -> viewModel.changeText(event.text)
                is AddTalkTopicUiEvent.ClickAddTalkTopic -> TODO()
                is AddTalkTopicUiEvent.ClickNavigateBack -> navigateBack()
                is AddTalkTopicUiEvent.ClickSetPublic -> viewModel.setPublic()
                is AddTalkTopicUiEvent.ClickTalkTopicCategory ->
                    viewModel.selectCategory(event.category)
            }
        }
    }

    AddTalkTopicScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}