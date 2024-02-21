package com.sghore.needtalk.presentation.ui.join_screen

import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.TimerInfo
import kotlinx.coroutines.flow.collectLatest

// TODO: fix. 여럿이서 참가하기 눌렀을 때 오류 발생
@Composable
fun JoinRoute(
    viewModel: JoinViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    navigateToTimerScreen: (TimerInfo) -> Unit,
    showSnackBar: suspend (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(
        key1 = viewModel.uiEvent,
        block = {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is JoinUiEvent.ClickBackArrow -> {
                        navigateUp()
                    }

                    is JoinUiEvent.ClickJoin -> {
                        navigateToTimerScreen(event.timerInfo)
                    }

                    is JoinUiEvent.ClickResearch -> {
                        viewModel.researchDevice(context.packageName)
                    }

                    is JoinUiEvent.LoadTimerInfo -> {
                        viewModel.loadTimerInfo(event.index)
                    }

                    is JoinUiEvent.ShowSnackBar -> {
                        showSnackBar(event.message)
                    }
                }
            }
        }
    )

    Surface {
        JoinScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent
        )
    }
}