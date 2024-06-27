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
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import com.sghore.needtalk.presentation.ui.timer_screen.TimerReadyDialog
import com.sghore.needtalk.presentation.ui.timer_screen.TimerScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiEvent
import com.sghore.needtalk.presentation.ui.timer_screen.WarningDialog
import com.sghore.needtalk.util.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun HostTimerRoute(
    viewModel: HostTimerViewModel = hiltViewModel(),
    userData: UserData?,
    navigateUp: () -> Unit,
    showSnackBar: suspend (String) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    var service: HostTimerService? by remember { mutableStateOf(null) }

    // 서비스 바인드 동작
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
                service = (binder as HostTimerService.LocalBinder).getService() // 서비스 바인드
                service?.startAdvertising( // 다른 장치들이 찾을 수 있도록 광고 수행
                    initTimerCmInfo = uiState.timerCommunicateInfo,
                    onError = {}
                )
            }

            override fun onServiceDisconnected(className: ComponentName?) {
                service = null
            }
        }
    }
    // 센서 동작
    val sensorListener = remember {
        object : SensorEventListener2 {
            override fun onSensorChanged(event: SensorEvent?) {
                val eventZ = event?.values?.get(2) ?: 0f
                val timerActionState = uiState.timerCommunicateInfo.timerActionState

                // 타이머가 동작되지 않았으며, 기기가 놓여져있는 경우
                if (eventZ > SensorManager.GRAVITY_EARTH * Constants.DEVICE_FLIP && !uiState.isFlip) {
                    // 타이머 준비 다이얼로그인 경우에만
                    if (timerActionState is TimerActionState.TimerReady) {
                        viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                    }

                    vibrate(context)
                    service?.deviceFlip(true)
                    viewModel.flipState(true)
                } else if (eventZ < Constants.DEVICE_NON_FLIP && uiState.isFlip) { // 타이머가 동작이 되었으며, 기기가 들려진 경우
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
            service?.stopForegroundService() // 포그라운드 서비스 중지

            if (timerActionState != TimerActionState.TimerWaiting
                && timerActionState != TimerActionState.TimerFinished
                && timerActionState !is TimerActionState.TimerError
            ) { // 타이머 상태가 해당 상태가 아닌경우에만 센서 재동작
                startSensor(context, sensorListener)
            }
        },
        onStop = {
            // 포그라운드 서비스 동작
            service?.startForegroundService()
            // 센서 동작 중지
            stopSensor(context, sensorListener)
        },
        onDispose = {
            // 서비스 중지
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

                                    is TimerActionState.TimerPause,
                                    is TimerActionState.StopWatchPause ->
                                        "대화에 집중 하고 있어요.\n정말로 나가시겠습니까?"

                                    else -> ""
                                }

                            viewModel.setDialogScreen(DialogScreen.DialogWarning(message))
                        }

                        is TimerUiEvent.ClickTopicCategory -> {
//                            viewModel.setDialogScreen(
//                                DialogScreen.DialogTalkTopics(
//                                    event.topicCategory,
//                                    event.groupCode
//                                )
//                            )
                        }

                        is TimerUiEvent.CancelPinnedTopic -> {
//                            service?.pinnedTalkTopic(null)
                        }

                        is TimerUiEvent.ClickStart -> {
                            if (event.isEnabled) {
//                                viewModel.saveOtherUserData()

                                service?.timerReady(onOpenDialog = viewModel::setDialogScreen)
                                startSensor(context, sensorListener)
                            } else {
                                showSnackBar("멤버가 모두 모이지 않았습니다.")
                            }
                        }

                        is TimerUiEvent.ClickFinished -> {
                            if (uiState.timerCommunicateInfo.isTimer) {
                                viewModel.setDialogScreen(
                                    DialogScreen.DialogWarning(
                                        "아직 대화중인 인원들이 있어요\n" +
                                                "정말로 나가시겠습니까?"
                                    )
                                )
                            } else {
//                                viewModel.saveTalkHistory {
//                                    Toast.makeText(context, it, Toast.LENGTH_SHORT)
//                                        .show()
//                                }

                                service = null
                                navigateUp()
                            }
                        }
                    }
                }
            }

            launch {
                service?.timerCmInfo?.collectLatest { // 타이머 정보 업데이트
                    if (it.timerActionState is TimerActionState.TimerError) {
                        service?.deviceFlip(false)
                        stopSensor(context, sensorListener)

                        viewModel.setDialogScreen(
                            DialogScreen.DialogWarning(
                                it.timerActionState.errorMsg,
                                isError = true
                            )
                        )
                    } else if (it.timerActionState is TimerActionState.TimerFinished) {
                        stopSensor(context, sensorListener)
                    }

                    // UI 업데이트
                    viewModel.updateTimerCommunicateInfo(it)
                }
            }
        })

    BackHandler {}

    Surface {
        TimerScreen(
            userData = userData,
            uiState = uiState,
            onEvent = viewModel::handelEvent,
            isHost = true
        )

        when (val dialogScreen = uiState.dialogScreen) {
            is DialogScreen.DialogWarning -> { // 경고 다이얼로그
                if (dialogScreen.isError) { // 오류 발생 시
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
//                            viewModel.saveTalkHistory {
//                                Toast.makeText(context, it, Toast.LENGTH_SHORT)
//                                    .show()
//                            }

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
//                            viewModel.saveTalkHistory {
//                                Toast.makeText(context, it, Toast.LENGTH_SHORT)
//                                    .show()
//                            }

                            viewModel.setDialogScreen(DialogScreen.DialogDismiss)

                            service = null
                            navigateUp()
                        },
                        negativeButtonText = "취소",
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

// 서비스 시작
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

// 서비스 중지
private fun stopService(context: Context, connection: ServiceConnection) {
    context.unbindService(connection)
}

// 센서 시작
private fun startSensor(context: Context, sensorListener: SensorEventListener2) {
    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    val sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

    sensorManager.registerListener(
        sensorListener,
        sensor,
        SensorManager.SENSOR_DELAY_NORMAL
    )
}

// 센서 중지
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