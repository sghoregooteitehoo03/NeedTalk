package com.sghore.needtalk.presentation.ui.statics_screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun StaticsRoute(
    viewModel: StaticsViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is StaticsUiEvent.ClickBackArrow -> {
                    navigateUp()
                }

                is StaticsUiEvent.ClickChangeDate -> {
                    viewModel.changeTimeRange(event.startTime, event.endTime)
                }
            }
        }
    }

    Surface {
        StaticsScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent
        )
    }
}