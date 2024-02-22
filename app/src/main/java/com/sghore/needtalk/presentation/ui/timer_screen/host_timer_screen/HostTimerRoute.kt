package com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener2
import android.hardware.SensorManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
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
import com.sghore.needtalk.component.HostTimerService
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
fun HostTimerRoute(
    viewModel: HostTimerViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    showSnackBar: suspend (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var service: HostTimerService? by remember { mutableStateOf(null) }

    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
                service = (binder as HostTimerService.LocalBinder).getService()
                service?.startAdvertising(
                    initTimerCmInfo = uiState.timerCommunicateInfo,
                    onOpenDialog = viewModel::setDialogScreen,
                    onError = {}
                )
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                service = null
            }
        }
    }
    val sensorListener = remember {
        object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                val eventZ = event?.values?.get(2) ?: 0f
                val timerActionState = uiState.timerCommunicateInfo.timerActionState
                Log.i("Check", "$eventZ")

                // 타이머가 동작되지 않았으며, 기기가 놓여져있는 경우
                if (eventZ > SensorManager.GRAVITY_EARTH * 0.95f && !uiState.isFlip) {
                    if (timerActionState is TimerActionState.TimerReady) {
                        viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                    }

                    vibrate(context)
                    service?.deviceFlip(true)
                    viewModel.flipState(true)
                } else if (eventZ < 7f && uiState.isFlip) { // 타이머가 동작이 되었으며, 기기가 들려진 경우
                    service?.deviceFlip(false)
                    viewModel.flipState(false)
                }
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

            override fun onFlushCompleted(p0: Sensor?) {}
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
            val timerActionState = uiState.timerCommunicateInfo.timerActionState
            service?.stopForegroundService()

            if (timerActionState != TimerActionState.TimerWaiting
                && timerActionState != TimerActionState.TimerFinished
            ) {
                startSensor(context, sensorListener)
            }
        },
        onStop = {
            service?.startForegroundService()
            stopSensor(context, sensorListener)
        },
        onDispose = {
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
                                    is TimerActionState.TimerPause,
                                    is TimerActionState.StopWatchPause ->
                                        "대화에 집중하고 있어요\n정말로 나가시겠습니까?"

                                    else -> ""
                                }

                            viewModel.setDialogScreen(DialogScreen.DialogWarning(message))
                        }

                        is TimerUiEvent.ChangeTalkTopic -> {
                            viewModel.changeTalkTopic()
                        }

                        is TimerUiEvent.ClickStart -> {
                            if (event.isEnabled) {
                                viewModel.saveOtherUserData()

                                service?.timerReady(onOpenDialog = viewModel::setDialogScreen)
                                startSensor(context, sensorListener)
                            } else {
                                showSnackBar("멤버가 모두 모이지 않았습니다.")
                            }
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

                                service = null
                                navigateUp()
                            }
                        }
                    }
                }
            }

            launch {
                service?.timerCmInfo?.collectLatest {
                    if (it.timerActionState is TimerActionState.TimerError) {
                        service?.deviceFlip(false)
                        stopSensor(context, sensorListener)

                        viewModel.setDialogScreen(
                            DialogScreen.DialogWarning(
                                it.timerActionState.errorMsg,
                                isError = true
                            )
                        )
                    }

                    viewModel.updateTimerCommunicateInfo(it)
                }
            }
        })

    BackHandler {}

    Surface {
        TimerScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent,
            isHost = true
        )

        when (val dialogScreen = uiState.dialogScreen) {
            is DialogScreen.DialogWarning -> {
                if (dialogScreen.isError) {
                    stopSensor(context, sensorListener)

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

                            service = null
                            navigateUp()
                        },
                        isError = true,
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

                            service = null
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

private fun startSensor(context: Context, sensorListener: SensorEventListener2) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    sensorManager.registerListener(
        sensorListener,
        sensor,
        SensorManager.SENSOR_DELAY_NORMAL
    )
}

private fun stopSensor(context: Context, sensorListener: SensorEventListener2) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    sensorManager.unregisterListener(
        sensorListener,
        sensor,
    )
}

@Suppress("DEPRECATION")
private fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Vibrator::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(200, 100))
    } else {
        vibrator.vibrate(200)
    }
}