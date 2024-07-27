package com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen

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
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.component.ClientTimerService
import com.sghore.needtalk.domain.model.PinnedTalkTopic
import com.sghore.needtalk.domain.model.TalkResult
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import com.sghore.needtalk.presentation.ui.UiScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerReadyDialog
import com.sghore.needtalk.presentation.ui.timer_screen.TimerScreen
import com.sghore.needtalk.presentation.ui.timer_screen.TimerUiEvent
import com.sghore.needtalk.presentation.ui.timer_screen.WarningDialog
import com.sghore.needtalk.presentation.ui.timer_screen.pinned_talktopic_dialog.PinnedTalkTopicDialog
import com.sghore.needtalk.util.Constants
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@Composable
fun ClientTimerRoute(
    viewModel: ClientTimerViewModel = hiltViewModel(),
    userData: UserData?,
    navigateUp: () -> Unit,
    navigateResultScreen: (String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    var service: ClientTimerService? by remember { mutableStateOf(null) }
    var isSensorStart by remember { mutableStateOf(false) } // 타이머 준비 여부

    // 서비스 바인드 동작
    val connection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(className: ComponentName?, binder: IBinder?) {
                service = (binder as ClientTimerService.LocalBinder).getService() // 서비스 바인드

                service?.startForegroundService() // 포그라운드 서비스 시작
                service?.connectToHost( // 호스트에 연결
                    userData = userData,
                    hostEndpointId = uiState.hostEndpointId,
                    onRejectJoin = viewModel::setDialogScreen,
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
                    service?.deviceFlip(isFlip = true, hostEndpointId = uiState.hostEndpointId)
                    viewModel.flipState(true)
                } else if (eventZ < Constants.DEVICE_NON_FLIP && uiState.isFlip) { // 타이머가 동작이 되었으며, 기기가 들려진 경우
                    service?.deviceFlip(isFlip = false, hostEndpointId = uiState.hostEndpointId)
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

            if (timerActionState != TimerActionState.TimerWaiting
                && timerActionState != TimerActionState.TimerFinished
                && timerActionState !is TimerActionState.TimerError
            ) {
                // 타이머 상태가 해당 상태가 아닌경우에만 센서 재동작
                startSensor(context, sensorListener)
            }
        },
        onStop = {
            // 센서 동작 중지
            stopSensor(context, sensorListener)
        },
        onDispose = {
            // 서비스 중지
            service?.stopForegroundService()
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
                                        "대화에 집중하고 있어요\n정말로 나가시겠습니까?"

                                    else -> ""
                                }

                            viewModel.setDialogScreen(DialogScreen.DialogWarning(message))
                        }

                        is TimerUiEvent.ClickFinished -> {
                            if (!uiState.timerCommunicateInfo.isTimer) {
                                viewModel.setDialogScreen(
                                    DialogScreen.DialogWarning(
                                        "아직 대화중인 인원들이 있어요\n" +
                                                "정말로 나가시겠습니까?"
                                    )
                                )
                            } else {
                                viewModel.finishedTalk(
                                    currentUserId = userData?.userId ?: "",
                                    recordFilePath = service?.outputFilePath ?: "",
                                    navigateOtherScreen = { talkResult ->
                                        if (talkResult != null) {
                                            navigateToResultScreen(
                                                talkResult = talkResult,
                                                navigate = navigateResultScreen
                                            )
                                        }
                                    }
                                )

                                service = null
                            }
                        }

                        is TimerUiEvent.AddPinnedTalkTopic -> {
                            viewModel.setDialogScreen(DialogScreen.DialogPinnedTalkTopic)
                        }

                        is TimerUiEvent.CancelPinnedTopic -> {
                            service?.pinnedTalkTopic(null, uiState.hostEndpointId)
                        }
                    }
                }
            }
            launch {
                service?.timerCmInfo?.collectLatest { // 타이머 정보 업데이트
                    when (it.timerActionState) {
                        is TimerActionState.TimerError -> {
                            stopSensor(
                                context,
                                sensorListener
                            )

                            viewModel.setDialogScreen(
                                DialogScreen.DialogWarning(
                                    it.timerActionState.errorMsg,
                                    isError = true
                                )
                            )
                        }

                        is TimerActionState.TimerReady -> {
                            if (!isSensorStart) {
                                viewModel.setDialogScreen(DialogScreen.DialogTimerReady)

                                startSensor(context, sensorListener)
                                isSensorStart = true
                            }
                        }

                        is TimerActionState.TimerFinished -> {
                            stopSensor(
                                context,
                                sensorListener
                            )
                        }

                        else -> {}
                    }

                    // UI 업데이트
                    viewModel.updateTimerCommunicateInfo(
                        timerCommunicateInfo = it,
                        onTimerStart = { saveOtherUser -> saveOtherUser(userData?.userId ?: "") }
                    )
                }
            }

            launch {
                service?.amplitudeFlow?.collectLatest { viewModel.updateAmplitudeValue(it) }
            }
        })

    BackHandler {}

    Surface {
        if (uiState.timerCommunicateInfo.participantInfoList.isNotEmpty()) {
            TimerScreen(
                userData = userData,
                uiState = uiState,
                onEvent = viewModel::handelEvent,
                isHost = false
            )
        } else { // 타이머 정보를 받아오는 중에 표시할 로딩 뷰
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = MaterialTheme.colors.onPrimary
                )
                DefaultButton(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    text = "나가기",
                    onClick = { navigateUp() }
                )
            }
        }

        when (val dialogScreen = uiState.dialogScreen) {
            is DialogScreen.DialogWarning -> { // 경고 다이얼로그
                if (dialogScreen.isError) { // 오류 발생 시
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
                                val timerActionState = service?.timerCmInfo?.value?.timerActionState
                                if (timerActionState != TimerActionState.TimerWaiting) {
                                    viewModel.finishedTalk(
                                        currentUserId = userData?.userId ?: "",
                                        recordFilePath = service?.outputFilePath ?: "",
                                        navigateOtherScreen = { talkResult ->
                                            if (talkResult != null) {
                                                navigateToResultScreen(
                                                    talkResult = talkResult,
                                                    navigate = navigateResultScreen
                                                )
                                            } else {
                                                navigateUp()
                                                Toast.makeText(
                                                    context,
                                                    "5분 미만의 대화는 기록되지 않습니다.",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    )
                                } else {
                                    navigateUp()
                                }
                            }

                            viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                            service = null
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
                            val timerActionState = service?.timerCmInfo?.value?.timerActionState
                            if (timerActionState != TimerActionState.TimerWaiting) {
                                viewModel.finishedTalk(
                                    currentUserId = userData?.userId ?: "",
                                    recordFilePath = service?.outputFilePath ?: "",
                                    navigateOtherScreen = { talkResult ->
                                        if (talkResult != null) {
                                            navigateToResultScreen(
                                                talkResult = talkResult,
                                                navigate = navigateResultScreen
                                            )
                                        } else {
                                            navigateUp()
                                            Toast.makeText(
                                                context,
                                                "5분 미만의 대화는 기록되지 않습니다.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                )
                            } else {
                                navigateUp()
                            }

                            viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                            service = null
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

            is DialogScreen.DialogPinnedTalkTopic -> {
                PinnedTalkTopicDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .padding(14.dp),
                    userId = userData?.userId ?: "",
                    onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                    onPinnedTalkTopic = {
                        val pinnedTalkTopic = PinnedTalkTopic(
                            talkTopic = it,
                            pinnedUserId = userData?.userId ?: "",
                            pinnedUserName = userData?.name ?: ""
                        )

                        // TODO: feat: 이미 설정된게 있으면 무시
                        service?.pinnedTalkTopic(
                            pinnedTalkTopic,
                            hostEndpointId = uiState.hostEndpointId
                        )
                    }
                )
            }

            else -> {}
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
private fun navigateToResultScreen(
    talkResult: TalkResult,
    navigate: (String) -> Unit
) {
    val talkResultJson = Json.encodeToString(TalkResult.serializer(), talkResult)
    val route = UiScreen.ResultScreen.route + "?talkResult=${talkResultJson}"

    navigate(route)
}

// 서비스 시작
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