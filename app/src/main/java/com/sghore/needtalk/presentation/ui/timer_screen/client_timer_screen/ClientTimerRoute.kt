package com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.widget.Toast
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.component.ClientTimerService
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import com.sghore.needtalk.presentation.ui.timer_screen.TimerReadyDialog
import com.sghore.needtalk.presentation.ui.timer_screen.TimerScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiEvent
import com.sghore.needtalk.presentation.ui.timer_screen.WarningDialog
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ClientTimerRoute(
    viewModel: ClientTimerViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var service: ClientTimerService? by remember { mutableStateOf(null) }

    val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
            service = (binder as ClientTimerService.LocalBinder).getService()
            service?.connectToHost(
                userEntity = uiState.userEntity,
                hostEndpointId = uiState.hostEndpointId,
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
            service = null
            stopService(context = context, connection = connection)
        }
    )

    LaunchedEffect(
        key1 = service,
        block = {
            launch {
                viewModel.uiEvent.collectLatest { event ->
                    when (event) {
                        is TimerUiEvent.ClickExit -> {
                            val message =
                                when (uiState.timerCommunicateInfo.timerActionState) {
                                    is TimerActionState.TimerWaiting,
                                    is TimerActionState.TimerReady ->
                                        "아직 대화가 시작되지 않았어요\n정말로 나가시겠습니까?"

                                    is TimerActionState.TimerRunning,
                                    is TimerActionState.TimerStop,
                                    is TimerActionState.StopWatchStop ->
                                        "대화에 집중하고 있어요\n정말로 나가시겠습니까?"

                                    else -> ""
                                }

                            viewModel.setDialogScreen(DialogScreen.DialogWarning(message))
                        }

                        is TimerUiEvent.ChangeTalkTopic -> {
                            viewModel.changeTalkTopic()
                        }

                        is TimerUiEvent.ClickFinished -> {
                            if (uiState.timerCommunicateInfo.isStopWatch) {
                                viewModel.setDialogScreen(
                                    DialogScreen.DialogWarning(
                                        "아직 대화중인 인원들이 있어요\n" +
                                                "정말로 나가시겠습니까?"
                                    )
                                )
                            } else {
                                viewModel.saveTalkHistory {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT)
                                        .show()
                                }
                                navigateUp()
                            }
                        }
                    }
                }
            }
            launch {
                service?.timerCmInfo?.collectLatest {
                    viewModel.updateTimerCommunicateInfo(it)
                }
            }
        })

    BackHandler {}

    Surface {
        TimerScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent,
            isHost = false
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
                            if (!dialogScreen.isReject) {
                                viewModel.saveTalkHistory {
                                    Toast.makeText(context, it, Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }

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
                            viewModel.saveTalkHistory {
                                Toast.makeText(context, it, Toast.LENGTH_SHORT)
                                    .show()
                            }

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
    Intent(context, ClientTimerService::class.java).also { intent ->
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