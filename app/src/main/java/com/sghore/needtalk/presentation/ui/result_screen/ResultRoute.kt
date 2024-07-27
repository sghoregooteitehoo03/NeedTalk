package com.sghore.needtalk.presentation.ui.result_screen

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ResultRoute(
    viewModel: ResultViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val systemUiController = rememberSystemUiController()
    val statusbarColor = MaterialTheme.colors.background
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

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ResultUiEvent.ChangeTalkTitle -> viewModel.changeTalkTitle(event.title)
                is ResultUiEvent.AddFriend -> viewModel.addFriend(
                    userId = event.userId,
                    index = event.index
                )

                ResultUiEvent.ClickConfirm -> {
                    viewModel.saveTalkHistory()
                    navigateUp()
                }
            }
        }
    }

    Surface {
        ResultScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent
        )
    }
}