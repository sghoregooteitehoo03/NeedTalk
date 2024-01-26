package com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiEvent
import com.sghore.needtalk.presentation.ui.timer_screen.WarningDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ClientTimerRoute(
    viewModel: ClientTimerViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(
        key1 = viewModel.uiEvent,
        block = {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is TimerUiEvent.ClickExit -> {
                        viewModel.setDialogScreen(
                            DialogScreen.DialogWarning(
                                message = "아직 대화가 시작되지 않았어요\n정말로 나가시겠습니까?"
                            )
                        )
                    }
                }
            }
        })

    BackHandler {
        viewModel.handelEvent(TimerUiEvent.ClickExit)
    }

    Surface {
        TimerScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent
        )

        when (val dialogScreen = uiState.dialogScreen) {
            is DialogScreen.DialogWarning -> {
                if (dialogScreen.isError) {
                    WarningDialog(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .fillMaxWidth()
                            .padding(14.dp),
                        message = dialogScreen.message,
                        possibleButtonText = "나가기",
                        onPossibleClick = {
                            viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                            navigateUp()
                        },
                        onDismiss = {}
                    )
                } else {
                    WarningDialog(
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .fillMaxWidth()
                            .padding(14.dp),
                        message = dialogScreen.message,
                        possibleButtonText = "나가기",
                        onPossibleClick = {
                            viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                            navigateUp()
                        },
                        negativeButtonText = "취소",
                        onNegativeClick = {
                            viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                        },
                        onDismiss = {
                            viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                        }
                    )
                }
            }

            else -> {}
        }
    }
}