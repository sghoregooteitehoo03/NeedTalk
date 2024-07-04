package com.sghore.needtalk.presentation.ui.result_screen

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle

@Composable
fun ResultRoute(
    viewModel: ResultViewModel = hiltViewModel()
) {
    val systemUiController = rememberSystemUiController()
    val statusbarColor = MaterialTheme.colors.background
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    DisposableEffectWithLifeCycle(
        onCreate = {
            systemUiController.setStatusBarColor(
                color = statusbarColor,
                darkIcons = true
            )
        },
        onDispose = {}
    )

    Surface {
        ResultScreen(
            uiState = uiState
        )
    }
}