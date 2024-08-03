package com.sghore.needtalk.presentation.ui.start_screen

import androidx.compose.runtime.Composable

@Composable
fun StartRoute(
    navigateToCreateProfile: () -> Unit,
) {
    StartScreen(onStartClick = {
        navigateToCreateProfile()
    })
}