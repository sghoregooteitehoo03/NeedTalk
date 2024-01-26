package com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.timer_screen.TimerScreen

@Composable
fun ClientTimerRoute(
    viewModel: ClientTimerViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Surface {
        TimerScreen(uiState)
    }
}