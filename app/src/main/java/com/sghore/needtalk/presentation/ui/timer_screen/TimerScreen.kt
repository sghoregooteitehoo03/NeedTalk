package com.sghore.needtalk.presentation.ui.timer_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.holix.android.bottomsheetdialog.compose.BottomSheetBehaviorProperties
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.EmptyTalkUserInfo
import com.sghore.needtalk.presentation.ui.ParticipantInfoItem
import com.sghore.needtalk.presentation.ui.RoundedButton
import com.sghore.needtalk.util.parseMinuteSecond

@Composable
fun TimerScreen(
    userData: UserData?,
    uiState: TimerUiState,
//    onEvent: (TimerUiEvent) -> Unit,
    isHost: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.secondary)
    ) {
        val timerCmInfo = uiState.timerCommunicateInfo

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.28f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            when (val timerActionState = timerCmInfo.timerActionState) {
                !is TimerActionState.TimerRunning, !is TimerActionState.StopWatchRunning -> {
                    TimerWithButton(
                        timerTime = timerCmInfo.currentTime,
                        timerActionState = timerActionState,
                        isHost = isHost
                    )
                }

                else -> {}
            }
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.72f)
                    .padding(14.dp)
            ) {
                TimerStateInfo(
                    timerActionState = timerCmInfo.timerActionState,
                    isAvailableStart = timerCmInfo.participantInfoList.size == timerCmInfo.maxMember
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
            ) {
                Participants(
                    currentUser = userData,
                    participantInfoList = timerCmInfo.participantInfoList,
                    maxMember = timerCmInfo.maxMember
                )
            }
        }
    }
}

@Composable
fun TimerWithButton(
    modifier: Modifier = Modifier,
    timerTime: Long,
    timerActionState: TimerActionState,
    isHost: Boolean,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = parseMinuteSecond(timerTime),
            style = MaterialTheme.typography.h1.copy(
                color = MaterialTheme.colors.onSecondary,
            )
        )
        Spacer(modifier = Modifier.height(28.dp))
        Row {
            TimerButton(
                buttonText = "나가기",
                buttonIcon = painterResource(id = R.drawable.ic_exit),
                onClick = {}
            )
            if (isHost) {
                Spacer(modifier = Modifier.width(20.dp))
                TimerButton(
                    buttonText = "시작",
                    buttonIcon = painterResource(id = R.drawable.ic_start),
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun TimerButton(
    modifier: Modifier = Modifier,
    buttonText: String,
    buttonIcon: Painter,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    color = colorResource(id = R.color.orange_80),
                    shape = CircleShape
                )
                .clickable { onClick() },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = buttonIcon,
                contentDescription = "ButtonIcon",
                tint = MaterialTheme.colors.onSecondary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = buttonText,
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onSecondary
            )
        )
    }
}

@Composable
fun TimerStateInfo(
    modifier: Modifier = Modifier,
    timerActionState: TimerActionState,
    isAvailableStart: Boolean
) {
    val stateText = when (timerActionState) {
        is TimerActionState.TimerWaiting -> {
            if (isAvailableStart) {
                "멤버가 모두 들어왔어요.\n대화를 시작해보세요!"
            } else {
                "멤버가 모두 들어올 때 까지\n잠시 기다려주세요."
            }
        }

        is TimerActionState.TimerReady,
        is TimerActionState.TimerPause,
        is TimerActionState.StopWatchPause -> "모든 사용자가 휴대폰을 내려놓으면\n타이머가 시작됩니다."

        is TimerActionState.TimerFinished -> "즐거운 대화가 되셨나요?\n지정하신 타이머가 끝났어요."
        else -> ""
    }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .padding(top = 20.dp, bottom = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stateText,
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary.copy(alpha = 0.6f),
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun Participants(
    modifier: Modifier = Modifier,
    currentUser: UserData?,
    participantInfoList: List<ParticipantInfo?>,
    maxMember: Int
) {
    Column(modifier = modifier) {
        val maxWidth = LocalConfiguration.current.screenWidthDp.dp.minus(36.dp)

        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(10.dp)
        ) {
            items(maxMember) { index ->
                val participantInfo = participantInfoList.getOrNull(index)
                if (participantInfo != null) {
                    ParticipantInfoItem(
                        modifier = Modifier
                            .padding(4.dp)
                            .width(maxWidth.div(2)),
                        participantInfo = participantInfo,
                        isCurrentUser = (currentUser?.userId ?: "") == participantInfo.userId
                    )
                } else {
                    EmptyTalkUserInfo(
                        modifier = Modifier
                            .padding(4.dp)
                            .width(maxWidth.div(2)),
                        content = {}
                    )
                }
            }
        }
    }
}

@Composable
fun WarningDialog(
    modifier: Modifier = Modifier,
    message: String,
    possibleButtonText: String,
    negativeButtonText: String = "",
    onPossibleClick: () -> Unit,
    onNegativeClick: () -> Unit = {},
    isError: Boolean = false,
    onDismiss: () -> Unit
) {
    BottomSheetDialog(
        onDismissRequest = onDismiss,
        properties = BottomSheetDialogProperties(
            behaviorProperties = BottomSheetBehaviorProperties(isDraggable = !isError)
        )
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(80.dp),
                painter = painterResource(id = R.drawable.ic_warning),
                contentDescription = "Warning",
                tint = colorResource(id = R.color.gray)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.h5.copy(
                    color = colorResource(id = R.color.gray),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            RoundedButton(
                modifier = Modifier.fillMaxWidth(),
                text = possibleButtonText,
                color = MaterialTheme.colors.secondary,
                textStyle = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSecondary
                ),
                paddingValues = PaddingValues(14.dp),
                onClick = onPossibleClick
            )
            if (negativeButtonText.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                RoundedButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 2.dp,
                            color = colorResource(id = R.color.light_gray),
                            shape = MaterialTheme.shapes.large
                        ),
                    text = negativeButtonText,
                    color = Color.Transparent,
                    textStyle = MaterialTheme.typography.body1.copy(
                        color = colorResource(id = R.color.gray)
                    ),
                    paddingValues = PaddingValues(14.dp),
                    onClick = onNegativeClick
                )
            }
        }
    }
}

@Composable
fun TimerReadyDialog(
    modifier: Modifier
) {
    BottomSheetDialog(
        onDismissRequest = {},
        properties = BottomSheetDialogProperties(
            behaviorProperties = BottomSheetBehaviorProperties(isDraggable = false)
        )
    ) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(120.dp),
                painter = painterResource(id = R.drawable.phone_image),
                contentDescription = "",
                contentScale = ContentScale.FillWidth
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "모든 사용자가 휴대폰을 내려놓으면\n타이머가 시작됩니다.",
                style = MaterialTheme.typography.h5.copy(
                    color = colorResource(id = R.color.gray),
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}