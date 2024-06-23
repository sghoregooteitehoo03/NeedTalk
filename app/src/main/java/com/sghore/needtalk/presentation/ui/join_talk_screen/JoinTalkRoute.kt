package com.sghore.needtalk.presentation.ui.join_talk_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sghore.needtalk.domain.model.TimerInfo
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import com.sghore.needtalk.presentation.ui.theme.Orange50
import kotlinx.coroutines.flow.collectLatest

// TODO: fix. 여럿이서 참가하기 눌렀을 때 오류 발생
@Composable
fun JoinTalkRoute(
    viewModel: JoinTalkViewModel = hiltViewModel(),
    userData: UserData?,
    navigateUp: () -> Unit,
    navigateToTimerScreen: (TimerInfo) -> Unit,
    showSnackBar: suspend (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()

    DisposableEffectWithLifeCycle(
        onCreate = {
            systemUiController.setStatusBarColor(
                color = Orange50,
                darkIcons = false
            )
        },
        onDispose = {}
    )

    LaunchedEffect(
        key1 = viewModel.uiEvent,
        block = {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is JoinTalkUiEvent.ClickBackArrow -> {
                        navigateUp()
                    }

                    is JoinTalkUiEvent.ClickJoin -> {
                        // TODO: 타이머 화면 구현 후 구현
//                        navigateToTimerScreen(event.timerInfo)
                    }

                    is JoinTalkUiEvent.ClickResearch -> {
                        viewModel.researchDevice(context.packageName)
                    }

                    is JoinTalkUiEvent.LoadTimerInfo -> {
                        if (userData != null) {
                            viewModel.loadTimerInfo(
                                userId = userData.userId,
                                index = event.index
                            )
                        }
                    }

                    is JoinTalkUiEvent.ShowSnackBar -> {
                        showSnackBar(event.message)
                    }
                }
            }
        }
    )

    JoinTalkScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}