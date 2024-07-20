package com.sghore.needtalk.presentation.ui.add_highlight_screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddHighlightRoute(
    viewModel: AddHighlightViewModel = hiltViewModel(),
    showSnackBar: suspend (String) -> Unit,
    navigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is AddHighlightUiEvent.ClickNavigateUp -> navigateUp()
                is AddHighlightUiEvent.ChangeTitle -> viewModel.changeTitle(event.title)
                is AddHighlightUiEvent.ChangePlayerTime ->
                    viewModel.changeTime(event.startTime, event.endTime)

                is AddHighlightUiEvent.ClickPlayOrPause -> {
                    if (event.isPlay) {
                        viewModel.playRecord()
                    } else {
                        viewModel.pauseRecord()
                    }
                }

                is AddHighlightUiEvent.ClickComplete -> viewModel.addHighlight()

                is AddHighlightUiEvent.AlertError -> { showSnackBar(event.message) }
                is AddHighlightUiEvent.SuccessAddHighlight -> { navigateUp() }
            }
        }
    }

    DisposableEffectWithLifeCycle(
        onResume = {
            // mediaPlayer 재정의
            viewModel.preparePlayer(uiState.recordFile?.path ?: "")
        },
        onStop = {
            viewModel.finishPlayer()
        },
        onDispose = {}
    )

    Surface {
        AddHighlightScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent
        )
    }
}