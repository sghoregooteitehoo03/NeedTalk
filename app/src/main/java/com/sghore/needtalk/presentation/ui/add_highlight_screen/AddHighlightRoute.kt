package com.sghore.needtalk.presentation.ui.add_highlight_screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun AddHighlightRoute(
    viewModel: AddHighlightViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    DisposableEffectWithLifeCycle(
        onStop = { viewModel.pauseRecord() },
        onDispose = { viewModel.finishPlayer() }
    )

    Surface {
        AddHighlightScreen(uiState = uiState)
    }
}