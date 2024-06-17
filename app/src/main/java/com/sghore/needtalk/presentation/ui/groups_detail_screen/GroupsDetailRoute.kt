package com.sghore.needtalk.presentation.ui.groups_detail_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

// TODO: UI Event 구현 중
@Composable
fun GroupsDetailRoute(
    viewModel: GroupsDetailViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                GroupsDetailUiEvent.ClickNavigateUp -> navigateUp()
                GroupsDetailUiEvent.SelectEdit -> TODO()
                GroupsDetailUiEvent.SelectRemove -> TODO()
                GroupsDetailUiEvent.ClickGroupItem -> TODO()
            }
        }
    }

    GroupsDetailScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvent
    )
}