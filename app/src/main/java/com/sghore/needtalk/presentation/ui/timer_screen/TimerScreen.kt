package com.sghore.needtalk.presentation.ui.timer_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.presentation.ui.NameTag
import com.sghore.needtalk.presentation.ui.RoundedButton
import com.sghore.needtalk.presentation.ui.theme.Green50
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.presentation.ui.theme.Orange50
import com.sghore.needtalk.presentation.ui.theme.Orange80
import com.sghore.needtalk.util.calcDominantColor
import com.sghore.needtalk.util.parseMinuteSecond

// TODO: 타이머 끝났을 때 보여줄 화면 구현
@Composable
fun TimerScreen(
    uiState: TimerUiState,
    onEvent: (TimerUiEvent) -> Unit,
    isHost: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            contentAlignment = Alignment.Center
        ) {
            val musicInfo = uiState.timerCommunicateInfo?.musicInfo
            MusicInfo(
                thumbnailImage = musicInfo?.thumbnailImage ?: "",
                title = musicInfo?.title ?: ""
            )
        }

        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 14.dp, bottom = 14.dp)
        ) {
            val (groupMember, timer, explainText, btnLayout) = createRefs()
            val timerCmInfo = uiState.timerCommunicateInfo
            val isEnabled = timerCmInfo?.participantInfoList?.size == timerCmInfo?.maxMember

            GroupMember(
                modifier = Modifier.constrainAs(groupMember) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                currentUser = uiState.userEntity,
                participantInfoList = timerCmInfo?.participantInfoList ?: listOf(),
                maxMember = timerCmInfo?.maxMember ?: 0,
                isTimerStart = timerCmInfo?.timerActionState != TimerActionState.TimerWaiting
            )
            TimerContent(
                modifier = Modifier.constrainAs(timer) {
                    top.linkTo(groupMember.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(explainText.bottom)
                },
                currentTime = timerCmInfo?.currentTime ?: 0L,
                maxTime = timerCmInfo?.maxTime ?: 0L,
                isRunning = timerCmInfo?.timerActionState != TimerActionState.TimerStop
            )
            when (timerCmInfo?.timerActionState) {
                is TimerActionState.TimerWaiting -> {
                    TimerExplain(
                        modifier = Modifier.constrainAs(explainText) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(btnLayout.top, margin = 46.dp)
                        },
                        title = if (isEnabled) {
                            "멤버가 모두 들어왔어요.\n대화를시작해보세요!"
                        } else {
                            "멤버가 모두 들어올 때 까지\n잠시 기다려주세요."
                        }
                    )
                }

                is TimerActionState.TimerStop -> {
                    TimerExplain(
                        modifier = Modifier.constrainAs(explainText) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(btnLayout.top, margin = 46.dp)
                        },
                        title = "현재 타이머가 정지되어있습니다.",
                        content = "모든 사용자가 휴대폰을 내려놓으면\n타이머가 다시 동작됩니다."
                    )
                }

                else -> {
                    TimerExplainWithButton(
                        modifier = Modifier.constrainAs(explainText) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(btnLayout.top, margin = 46.dp)
                        },
                        title = "이런 대화는 어떠세요?",
                        content = "여행 중에 먹은 가장 맛있었던 음식은 무엇이었나요?",
                        buttonIcon = painterResource(id = R.drawable.ic_refresh),
                        onClick = {}
                    )
                }
            }
            Row(modifier = Modifier.constrainAs(btnLayout) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 32.dp)
            }) {
                if (timerCmInfo?.timerActionState == TimerActionState.TimerFinished
                    || (timerCmInfo?.maxTime == -1L && timerCmInfo.currentTime >= 300000L)
                ) {
                    RoundedButton(
                        modifier = Modifier.width(110.dp),
                        text = "끝내기",
                        color = MaterialTheme.colors.secondary,
                        textStyle = MaterialTheme.typography.body1.copy(color = Color.White),
                        paddingValues = PaddingValues(14.dp),
                        onClick = { onEvent(TimerUiEvent.ClickFinished) }
                    )
                } else {
                    RoundedButton(
                        modifier = Modifier.width(110.dp),
                        text = "나가기",
                        color = colorResource(id = R.color.light_gray_200),
                        textStyle = MaterialTheme.typography.body1.copy(color = Color.White),
                        paddingValues = PaddingValues(14.dp),
                        onClick = { onEvent(TimerUiEvent.ClickExit) }
                    )
                }

                if (isHost && timerCmInfo?.timerActionState == TimerActionState.TimerWaiting) {
                    Spacer(modifier = Modifier.width(20.dp))
                    RoundedButton(
                        modifier = Modifier.width(110.dp),
                        text = "시작하기",
                        color = MaterialTheme.colors.secondary,
                        textStyle = MaterialTheme.typography.body1.copy(color = Color.White),
                        paddingValues = PaddingValues(14.dp),
                        onClick = { onEvent(TimerUiEvent.ClickStart(isEnabled)) }
                    )
                }
            }
        }
    }
}

@Composable
fun MusicInfo(
    modifier: Modifier = Modifier,
    thumbnailImage: String,
    title: String
) {
    val defaultColor = MaterialTheme.colors.onPrimary
    var dominantColor by remember {
        mutableStateOf(defaultColor)
    }
    val painter = if (thumbnailImage.isEmpty()) {
        dominantColor = colorResource(id = R.color.light_gray_200)
        painterResource(id = R.drawable.ic_no_music)
    } else {
        rememberAsyncImagePainter(
            model = thumbnailImage,
            onSuccess = { result ->
                calcDominantColor(result.result.drawable) { color ->
                    dominantColor = color
                }
            }
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (thumbnailImage.isEmpty()) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        color = colorResource(id = R.color.light_gray_200),
                        shape = RoundedCornerShape(8.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painter,
                    contentDescription = "NoMusic",
                    tint = Color.White
                )
            }
        } else {
            Image(
                modifier = Modifier
                    .size(26.dp)
                    .clip(RoundedCornerShape(8.dp)),
                painter = painter,
                contentDescription = thumbnailImage,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            modifier = Modifier.drawBehind {
                val strokeWidthPx = 2.dp.toPx()
                val verticalOffset = size.height
                drawLine(
                    color = dominantColor,
                    strokeWidth = strokeWidthPx,
                    cap = StrokeCap.Round,
                    start = Offset(0f, verticalOffset),
                    end = Offset(size.width - 3, verticalOffset)
                )
            },
            text = title,
            style = MaterialTheme.typography.h5.copy(color = dominantColor)
        )
    }
}

@Composable
fun GroupMember(
    modifier: Modifier = Modifier,
    currentUser: UserEntity?,
    participantInfoList: List<ParticipantInfo?>,
    maxMember: Int,
    isTimerStart: Boolean
) {
    Row(modifier = modifier) {
        repeat(participantInfoList.size) { index ->
            val user = participantInfoList[index]?.userEntity
            Spacer(modifier = Modifier.width(9.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NameTag(
                    name = user?.name ?: "",
                    color = Color(user?.color ?: 0),
                    interval = 4.dp,
                    colorSize = 10.dp,
                    textStyle = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onPrimary,
                        fontWeight = if (user?.userId == currentUser?.userId) {
                            FontWeight.ExtraBold
                        } else {
                            FontWeight.Normal
                        }
                    )
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(
                            color = colorResource(id = R.color.light_gray),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTimerStart) {
                        val ready = participantInfoList[index]?.isReady ?: false
                        Icon(
                            modifier = Modifier.size(20.dp),
                            painter = if (ready) {
                                painterResource(id = R.drawable.ic_check)
                            } else {
                                painterResource(id = R.drawable.ic_pause)
                            },
                            contentDescription = "",
                            tint = if (ready) {
                                Green50
                            } else {
                                colorResource(id = R.color.gray)
                            }

                        )
                    }
                }
            }
            Spacer(modifier = Modifier.width(9.dp))
        }
        repeat(maxMember - participantInfoList.size) {
            Spacer(modifier = Modifier.width(9.dp))
            Box(
                modifier = Modifier
                    .width(60.dp)
                    .height(18.dp)
                    .clip(CircleShape)
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(9.dp))
        }
    }
}

// TODO: 프로그래스 움직이는 애니메이션 구현
@Composable
fun TimerContent(
    modifier: Modifier = Modifier,
    currentTime: Long,
    maxTime: Long,
    isRunning: Boolean
) {
    val isStopwatch = remember { maxTime == -1L }
    val maxWidth = LocalConfiguration.current.screenWidthDp
    val progress = if (isStopwatch) {
        1f
    } else {
        (currentTime / maxTime.toFloat())
    }

    Column(
        modifier = modifier
            .padding(start = 14.dp, end = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = parseMinuteSecond(timeStamp = currentTime),
            style = MaterialTheme.typography.h1.copy(
                if (isRunning) {
                    Orange50
                } else {
                    colorResource(id = R.color.gray)
                }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val repeatTime = maxWidth / 8

            repeat(repeatTime + 1) { index ->
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .then(
                            if ((8.5.dp.value) * (index + 1) <= progress * maxWidth) {
                                Modifier
                                    .height(26.dp)
                                    .background(
                                        color = if (isRunning) {
                                            Orange50
                                        } else {
                                            colorResource(id = R.color.gray)
                                        },
                                        shape = CircleShape
                                    )
                            } else {
                                Modifier
                                    .height(26.dp)
                                    .background(
                                        color = colorResource(id = R.color.light_gray),
                                        shape = CircleShape
                                    )
                            }
                        )
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
        }
    }

}

@Composable
fun TimerExplain(
    modifier: Modifier = Modifier,
    title: String,
    content: String = ""
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5.copy(
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )
        )
        if (content.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.body2.copy(
                    color = colorResource(id = R.color.gray),
                    textAlign = TextAlign.Center
                )
            )
        }
    }
}

@Composable
fun TimerExplainWithButton(
    modifier: Modifier,
    title: String,
    content: String = "",
    buttonIcon: Painter,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerExplain(
            title = title,
            content = content,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Icon(
            modifier = Modifier
                .size(24.dp)
                .clickable { onClick() },
            painter = buttonIcon,
            contentDescription = "",
            tint = colorResource(id = R.color.gray)
        )
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
    onDismiss: () -> Unit
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
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
    BottomSheetDialog(onDismissRequest = {}) {
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

@Preview
@Composable
fun GroupMemberPreview() {
    NeedTalkTheme {
        val testUserList = listOf(
            ParticipantInfo(
                userEntity = UserEntity(
                    userId = "abc",
                    name = "아령하세요",
                    color = Color.Blue.toArgb()
                ),
                endpointId = "",
                isReady = false
            ),
            ParticipantInfo(
                userEntity = UserEntity(
                    userId = "idna",
                    name = "하이하이",
                    color = Color.Magenta.toArgb()
                ),
                endpointId = "",
                isReady = false
            )
        )
        GroupMember(
            currentUser = testUserList[1].userEntity,
            participantInfoList = testUserList,
            maxMember = 4,
            isTimerStart = false
        )
    }
}

@Preview
@Composable
fun MusicInfoPreview() {
    NeedTalkTheme {
        MusicInfo(
            thumbnailImage = "https://i.ytimg.com/vi/jfKfPfyJRdk/hq720.jpg?sqp=-oaymwEcCOgCEMoBSFXyq4qpAw4IARUAAIhCGAFwAcABBg==&rs=AOn4CLBAP74LKwgeVlcaO8dzN4FJFRwTVw",
            title = "로파이 뮤직"
        )
    }
}

@Preview
@Composable
fun TimerContentPreview() {
    NeedTalkTheme {
        TimerContent(
            currentTime = 3600000,
            maxTime = 3600000,
            isRunning = true
        )
    }
}

