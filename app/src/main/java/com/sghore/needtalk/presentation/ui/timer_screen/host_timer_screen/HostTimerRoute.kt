package com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
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
import com.sghore.needtalk.component.TimerService
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
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

    var service: TimerService? = remember { null }
    val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
            service = (binder as TimerService.LocalBinder).getService()
            // Nearby API 광고 시작
            Log.i("Check", service?.msg ?: "")
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
        onDispose = {
            service = null
            stopService(context = context, connection = connection)
        }
    )

    LaunchedEffect(
        key1 = viewModel.uiEvent,
        block = {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is TimerUiEvent.ClickExit -> {
                        viewModel.setDialogScreen(
                            DialogScreen.DialogWarning(
                                "아직 대화가 시작되지 않았어요\n정말로 나가시겠습니까?"
                            )
                        )
                    }

                    is TimerUiEvent.ClickStart -> {
                        if (event.isEnabled) {
                            viewModel.runTimer()
                        } else {
                            showSnackBar("멤버가 모두 모이지 않았습니다.")
                        }
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

            else -> {}
        }
    }
}

private fun startService(
    context: Context,
    connection: ServiceConnection
) {
    Intent(context, TimerService::class.java).also { intent ->
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