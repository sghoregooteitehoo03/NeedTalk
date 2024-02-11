package com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.component.HostTimerService
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import com.sghore.needtalk.presentation.ui.timer_screen.TimerReadyDialog
import com.sghore.needtalk.presentation.ui.timer_screen.TimerScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiEvent
import com.sghore.needtalk.presentation.ui.timer_screen.WarningDialog
import kotlinx.coroutines.flow.collectLatest

@Composable
fun HostTimerRoute(
    viewModel: HostTimerViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    showSnackBar: suspend (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var service: HostTimerService? = remember { null }
    val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
            service = (binder as HostTimerService.LocalBinder).getService()
            service?.startAdvertising(
                initTimerCmInfo = uiState.timerCommunicateInfo,
                onUpdateUiState = viewModel::updateTimerCommunicateInfo,
                onOpenDialog = viewModel::setDialogScreen,
                onError = {}
            )
        }

        override fun onServiceDisconnected(className: ComponentName?) {
            service = null
        }
    }

    DisposableEffectWithLifeCycle(
        onCreate = {
            startService(
                context = context,
                connection = connection
            )
        },
        onResume = {
            service?.stopForegroundService()
        },
        onStop = {
            service?.startForegroundService()
        },
        onDispose = {
            stopService(context = context, connection = connection)
        }
    )

    LaunchedEffect(
        key1 = viewModel.uiEvent,
        block = {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is TimerUiEvent.ClickExit -> {
                        val message = when (uiState.timerCommunicateInfo?.timerActionState) {
                            is TimerActionState.TimerWaiting, TimerActionState.TimerReady ->
                                "아직 대화가 시작되지 않았어요\n정말로 나가시겠습니까?"

                            is TimerActionState.TimerRunning, TimerActionState.TimerStop ->
                                "대화에 집중하고 있어요\n정말로 나가시겠습니까?"

                            else -> ""
                        }

                        viewModel.setDialogScreen(DialogScreen.DialogWarning(message))
                    }

                    is TimerUiEvent.ClickStart -> {
                        if (event.isEnabled) {
                            viewModel.saveOtherUserData()
                            service?.timerReady(
                                onUpdateUiState = viewModel::updateTimerCommunicateInfo,
                                onOpenDialog = viewModel::setDialogScreen,
                            )
                        } else {
                            showSnackBar("멤버가 모두 모이지 않았습니다.")
                        }
                    }

                    is TimerUiEvent.ClickFinished -> {
                        navigateUp()
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
            onEvent = viewModel::handelEvent,
            isHost = true
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

            is DialogScreen.DialogTimerReady -> {
                TimerReadyDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .fillMaxWidth()
                        .padding(top = 24.dp, bottom = 24.dp)
                )
            }

            else -> {}
        }
    }
}

private fun startService(
    context: Context,
    connection: ServiceConnection
) {
    Intent(context, HostTimerService::class.java).also { intent ->
        context.bindService(
            intent,
            connection,
            Context.BIND_AUTO_CREATE
        )
    }
}

private fun stopService(context: Context, connection: ServiceConnection) {
    context.unbindService(connection)
}