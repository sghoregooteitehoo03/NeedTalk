package com.sghore.needtalk.presentation.ui.join_talk_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.UserData
import kotlinx.coroutines.flow.collectLatest

// TODO: fix
//  . 여럿이서 참가하기 눌렀을 때 오류 발생
@Composable
fun JoinTalkRoute(
    viewModel: JoinTalkViewModel = hiltViewModel(),
    userData: UserData?,
    navigateUp: () -> Unit,
    navigateToTimerScreen: (String) -> Unit,
    showSnackBar: suspend (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val context = LocalContext.current

    LaunchedEffect(
        key1 = viewModel.uiEvent,
        block = {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is JoinTalkUiEvent.ClickBackArrow -> {
                        navigateUp()
                    }

                    is JoinTalkUiEvent.ClickJoin -> {
                        navigateToTimerScreen(event.hostEndPointId)
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