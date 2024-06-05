package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun TalkTopicsRoute(
    viewModel: TalkTopicsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    TalkTopicsScreen(
        uiState = uiState,
        onClick = {
            viewModel.setData()
        })
}