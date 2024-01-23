package com.sghore.needtalk.presentation.ui.host_timer_screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TimerRoute(
    viewModel: HostTimerViewModel = hiltViewModel()
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(text = "TimerRoute")
    }
}