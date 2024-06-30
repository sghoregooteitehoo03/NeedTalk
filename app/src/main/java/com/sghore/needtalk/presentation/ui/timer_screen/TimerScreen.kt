package com.sghore.needtalk.presentation.ui.timer_screen

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.holix.android.bottomsheetdialog.compose.BottomSheetBehaviorProperties
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.ParticipantInfo
import com.sghore.needtalk.domain.model.PinnedTalkTopic
import com.sghore.needtalk.domain.model.TimerActionState
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.EmptyTalkUserInfo
import com.sghore.needtalk.presentation.ui.ParticipantInfoItem
import com.sghore.needtalk.presentation.ui.TalkTopicCategoryTag
import com.sghore.needtalk.util.parseMinuteSecond

@Composable
fun TimerScreen(
    userData: UserData?,
    uiState: TimerUiState,
    onEvent: (TimerUiEvent) -> Unit,
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
                is TimerActionState.TimerRunning, is TimerActionState.StopWatchRunning -> {
                    TimerWithMic(
                        timerTime = timerCmInfo.currentTime,
                        isAllowMic = timerCmInfo.isAllowMic,
                        amplitudeValue = uiState.amplitudeValue
                    )
                }

                else -> {
                    TimerWithButton(
                        timerTime = timerCmInfo.currentTime,
                        isWaiting = timerCmInfo.timerActionState == TimerActionState.TimerWaiting,
                        isFinished = timerCmInfo.timerActionState == TimerActionState.TimerFinished ||
                                (timerCmInfo.timerActionState is TimerActionState.StopWatchPause && timerCmInfo.timerActionState.isFinished),
                        isHost = isHost,
                        onClickExit = { isFinished ->
                            if (isFinished) { // 타이머, 스톱워치 동작을 마무리 지었을 때
                                onEvent(TimerUiEvent.ClickFinished)
                            } else { // 중간에 나갔을 때
                                onEvent(TimerUiEvent.ClickExit)
                            }
                        },
                        onClickStart = {
                            onEvent(
                                TimerUiEvent.ClickStart(
                                    timerCmInfo.participantInfoList.size == timerCmInfo.maxMember
                                )
                            )
                        }
                    )
                }
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
                    .padding(top = 14.dp, start = 14.dp, end = 14.dp)
            ) {
                when (timerCmInfo.timerActionState) {
                    is TimerActionState.TimerRunning, is TimerActionState.StopWatchRunning -> {
                        PinnedTalkTopicItem(
                            pinnedTalkTopic = timerCmInfo.pinnedTalkTopic,
                            currentUserId = userData?.userId ?: "",
                            onClick = { onEvent(TimerUiEvent.AddPinnedTalkTopic) },
                            onCancelPinned = { onEvent(TimerUiEvent.CancelPinnedTopic) }
                        )
                    }

                    else -> {
                        TimerStateInfo(
                            timerActionState = timerCmInfo.timerActionState,
                            isAvailableStart = timerCmInfo.participantInfoList.size == timerCmInfo.maxMember
                        )
                    }
                }

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
fun TimerWithMic(
    modifier: Modifier = Modifier,
    timerTime: Long,
    amplitudeValue: Int,
    maxAmplitudeValue: Int = 32760,
    isAllowMic: Boolean
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (text, mic) = createRefs()
            Text(
                modifier = Modifier.constrainAs(text) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                text = parseMinuteSecond(timerTime),
                style = MaterialTheme.typography.h1.copy(
                    color = MaterialTheme.colors.onSecondary,
                )
            )
            if (isAllowMic) {
                Box(
                    modifier = Modifier
                        .constrainAs(mic) {
                            start.linkTo(text.end, 12.dp)
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                        }
                        .size(18.dp)
                        .background(
                            color = Color.Red,
                            shape = CircleShape
                        )
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            repeat(12) { index ->
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(24.dp)
                        .background(
                            // TODO: fix. 조건 값 조절하기
                            color = if (amplitudeValue >= maxAmplitudeValue * index / 12) {
                                colorResource(id = R.color.orange_80)
                            } else {
                                colorResource(id = R.color.light_orange)
                            },
                            shape = CircleShape
                        )
                )
                if (index < 11) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }
    }
}

@Composable
fun TimerWithButton(
    modifier: Modifier = Modifier,
    timerTime: Long,
    isWaiting: Boolean,
    isHost: Boolean,
    isFinished: Boolean,
    onClickExit: (Boolean) -> Unit,
    onClickStart: () -> Unit
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
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            if (isFinished) {
                TimerButton(
                    buttonText = "끝내기",
                    buttonIcon = painterResource(id = R.drawable.ic_check),
                    onClick = { onClickExit(true) }
                )
            } else {
                TimerButton(
                    buttonText = "나가기",
                    buttonIcon = painterResource(id = R.drawable.ic_exit),
                    onClick = { onClickExit(false) }
                )
            }
            if (isHost && isWaiting) {
                Spacer(modifier = Modifier.width(20.dp))
                TimerButton(
                    buttonText = "시작",
                    buttonIcon = painterResource(id = R.drawable.ic_start),
                    onClick = { onClickStart() }
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
fun PinnedTalkTopicItem(
    modifier: Modifier = Modifier,
    currentUserId: String,
    pinnedTalkTopic: PinnedTalkTopic?,
    onClick: () -> Unit,
    onCancelPinned: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .then(
                if (pinnedTalkTopic == null) {
                    Modifier.clickable { onClick() }
                } else {
                    Modifier
                }
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (pinnedTalkTopic == null) {
            Icon(
                modifier = Modifier.size(180.dp),
                painter = painterResource(id = R.drawable.ic_add),
                contentDescription = "PinTalkTopic",
                tint = colorResource(id = R.color.gray)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "대화주제를 지정하여\n대화를 나누어보세요.",
                style = MaterialTheme.typography.h4.copy(
                    color = colorResource(id = R.color.gray)
                )
            )
        } else {
            val talkTopic = pinnedTalkTopic.talkTopic
            ConstraintLayout(
                modifier = modifier
                    .fillMaxSize()
                    .shadow(2.dp, MaterialTheme.shapes.medium)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(14.dp),
            ) {
                val (tags, topic, pinned) = createRefs()
                Row(
                    modifier = Modifier.constrainAs(tags) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                ) {
                    val tagList = remember {
                        listOf(
                            talkTopic.category1.title,
                            talkTopic.category2?.title ?: "",
                            talkTopic.category3?.title ?: ""
                        ).filter { it.isNotEmpty() }
                    }

                    tagList.forEachIndexed { index, tagName ->
                        TalkTopicCategoryTag(
                            tagName = tagName,
                            paddingValues = PaddingValues(
                                top = 6.dp,
                                bottom = 6.dp,
                                start = 12.dp,
                                end = 12.dp
                            ),
                            textStyle = MaterialTheme.typography.subtitle1.copy(
                                color = MaterialTheme.colors.onPrimary
                            )
                        )
                        if (index < 1) {
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                    }
                }
                Text(
                    modifier = Modifier.constrainAs(topic) {
                        top.linkTo(tags.bottom, 16.dp)
                        bottom.linkTo(pinned.top, 16.dp)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
                    text = talkTopic.topic,
                    style = MaterialTheme.typography.h4.copy(
                        color = MaterialTheme.colors.onPrimary,
                        textAlign = TextAlign.Center
                    )
                )
                Column(
                    modifier = Modifier.constrainAs(pinned) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "(${pinnedTalkTopic.pinnedUserName}) 지정한 주제",
                        style = MaterialTheme.typography.body1.copy(
                            color = colorResource(id = R.color.gray)
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Icon(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable {
                                if (pinnedTalkTopic.pinnedUserId == currentUserId) {
                                    onCancelPinned()
                                } else {
                                    Toast
                                        .makeText(
                                            context,
                                            "고정한 사용자만 해제할 수 있습니다.",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                }
                            },
                        painter = painterResource(id = R.drawable.ic_pin),
                        contentDescription = "Pin",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            }
        }
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
                        isCurrentUser = (currentUser?.userId ?: "") == participantInfo.userId,
                        isReady = participantInfo.isReady
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
            Row(modifier = Modifier.fillMaxWidth()) {
                if (negativeButtonText.isNotEmpty()) {
                    val maxWidth = LocalConfiguration.current.screenWidthDp.dp.minus(40.dp)
                    Box(
                        modifier = Modifier
                            .width(maxWidth.div(2))
                            .height(46.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                color = colorResource(id = R.color.light_gray_200),
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = negativeButtonText,
                            style = MaterialTheme.typography.body1.copy(
                                color = MaterialTheme.colors.onSecondary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Box(
                        modifier = Modifier
                            .width(maxWidth.div(2))
                            .height(46.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(
                                color = MaterialTheme.colors.secondary,
                                shape = MaterialTheme.shapes.medium
                            )
                            .clickable { onPossibleClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = possibleButtonText,
                            style = MaterialTheme.typography.body1.copy(
                                color = MaterialTheme.colors.onSecondary
                            )
                        )
                    }
                } else {
                    DefaultButton(
                        text = possibleButtonText,
                        buttonHeight = 46.dp,
                        onClick = onPossibleClick
                    )
                }
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
                text = "휴대폰을 내려놓아주세요.",
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            )
        }
    }
}