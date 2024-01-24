package com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen

import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.needtalk.presentation.ui.timer_screen.TimerScreen

@Composable
fun HostTimerRoute(
    viewModel: HostTimerViewModel = hiltViewModel()
) {
    Surface {
        TimerScreen()
    }
}