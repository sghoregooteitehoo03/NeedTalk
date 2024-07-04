package com.sghore.needtalk.presentation.ui.result_screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ResultRoute(
    viewModel: ResultViewModel = hiltViewModel()
) {
    ResultScreen()
}