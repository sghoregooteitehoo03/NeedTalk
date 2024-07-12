package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.ConfirmWithCancelDialog
import com.sghore.needtalk.presentation.ui.ProfileImage
import com.sghore.needtalk.presentation.ui.SimpleInputDialog
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.util.getFileSizeToStr
import kotlinx.coroutines.delay
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TalkHistoryDetailScreen(
    uiState: TalkHistoryDetailUiState,
    onEvent: (TalkHistoryDetailUiEvent) -> Unit
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (topLayout, subMidLayout, midLayout, bottomLayout) = createRefs()

        val talkHistory = uiState.talkHistory
        val maxWidth = LocalConfiguration.current.screenWidthDp.dp
        Column(modifier = Modifier.constrainAs(topLayout) {
            top.linkTo(parent.top)
        }) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 14.dp, end = 14.dp),
            ) {
                var isExpended by remember { mutableStateOf(false) }
                val (navigateUp, title, more, dropdown) = createRefs()
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .constrainAs(navigateUp) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                        .clickable {
                            onEvent(TalkHistoryDetailUiEvent.ClickNavigateUp)
                        },
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = "NavigateUp",
                    tint = MaterialTheme.colors.onPrimary
                )
                Column(modifier = Modifier.constrainAs(title) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(navigateUp.end)
                    end.linkTo(more.start)
                }, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = talkHistory?.talkTitle ?: "",
                        style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = SimpleDateFormat(
                            "yy.MM.dd (E)",
                            Locale.KOREA
                        ).format(talkHistory?.createTimeStamp ?: 0L),
                        style = MaterialTheme.typography.subtitle1.copy(color = colorResource(id = R.color.gray))
                    )
                }
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .constrainAs(more) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
                        .clickable { isExpended = true },
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "More",
                    tint = MaterialTheme.colors.onPrimary
                )

                Box(modifier = Modifier.constrainAs(dropdown) {
                    top.linkTo(more.top)
                    end.linkTo(more.end)
                }) {
                    DropdownMenu(
                        expanded = isExpended,
                        onDismissRequest = { isExpended = false }
                    ) {
                        DropdownMenuItem(onClick = {
                            if (uiState.recordFile != null) {
                                onEvent(TalkHistoryDetailUiEvent.OptionInfo)
                            }
                            isExpended = false
                        }) {
                            Text(
                                text = "파일 정보",
                                style = MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.onPrimary
                                )
                            )
                        }
                        DropdownMenuItem(onClick = {
                            onEvent(TalkHistoryDetailUiEvent.OptionRenameTitle)
                            isExpended = false
                        }) {
                            Text(
                                text = "제목 수정",
                                style = MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.onPrimary
                                )
                            )
                        }
                        DropdownMenuItem(onClick = {
                            onEvent(TalkHistoryDetailUiEvent.OptionRemoveTalkHistory)
                            isExpended = false
                        }) {
                            Text(
                                text = "대화기록 삭제",
                                style = MaterialTheme.typography.body1.copy(
                                    color = MaterialTheme.colors.onPrimary
                                )
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            repeat(talkHistory?.users?.size ?: 0) { index ->
                val userData = talkHistory?.users?.get(index)
                if (userData != null) {
                    ParticipantUser(
                        modifier = Modifier.width(maxWidth.div(talkHistory.users.size)),
                        userData = userData
                    )
                }
            }
        }

        AudioRecordTime(
            modifier = Modifier.constrainAs(subMidLayout) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(midLayout.top, 12.dp)
            },
            maxRecordTime = talkHistory?.talkTime ?: 0L,
            currentRecordTime = uiState.playerTime
        )

        AudioRecordPlayer(
            modifier = Modifier
                .constrainAs(midLayout) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }
                .padding(start = 14.dp, end = 14.dp),
            maxRecordTime = 5000L,
            currentRecordTime = 0L,
            recordWaveForm = uiState.talkHistory?.recordAmplitude ?: emptyList(),
            onChangeRecordFile = {}
        )

        AudioRecordButtons(
            modifier = Modifier.constrainAs(bottomLayout) {
                top.linkTo(midLayout.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            isPlaying = uiState.isPlaying
        )
    }
}

@Composable
fun ParticipantUser(
    modifier: Modifier = Modifier,
    userData: UserData
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ProfileImage(
            backgroundSize = 56.dp,
            imageSize = 46.dp,
            profileImage = userData.profileImage
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = userData.name,
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        Spacer(modifier = Modifier.height(4.dp))
        FriendshipLayout(friendshipPoint = userData.friendshipPoint)
    }
}

@Composable
fun FriendshipLayout(
    modifier: Modifier = Modifier,
    friendshipPoint: Int
) {
    Column {
        Row {
            repeat(5) { index ->
                Image(
                    modifier = Modifier.size(14.dp),
                    painter = if ((index + 1) <= friendshipPoint) {
                        painterResource(id = R.drawable.filled_heart)
                    } else {
                        painterResource(id = R.drawable.unfilled_heart)
                    },
                    contentDescription = "friendshipPoint"
                )
            }
        }
        Row {
            repeat(5) { index ->
                Image(
                    modifier = Modifier.size(14.dp),
                    painter = if ((index + 6) <= friendshipPoint) {
                        painterResource(id = R.drawable.filled_heart)
                    } else {
                        painterResource(id = R.drawable.unfilled_heart)
                    },
                    contentDescription = "friendshipPoint"
                )
            }
        }
    }
}

@Composable
fun AudioRecordTime(
    modifier: Modifier = Modifier,
    maxRecordTime: Long,
    currentRecordTime: Long,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = SimpleDateFormat(
                "HH:mm:ss",
                Locale.KOREA
            ).format(maxRecordTime.minus(32400000)),
            style = MaterialTheme.typography.h5.copy(
                color = colorResource(id = R.color.gray)
            )
        )
        Text(
            text = SimpleDateFormat(
                "HH:mm:ss",
                Locale.KOREA
            ).format(currentRecordTime.minus(32400000)),
            style = MaterialTheme.typography.h3.copy(
                fontSize = 32.sp,
                color = MaterialTheme.colors.onPrimary,
                fontWeight = FontWeight.Bold
            )
        )
    }
}

// TODO: . 파형 가져와서 표시하기
@SuppressLint("ReturnFromAwaitPointerEventScope", "MultipleAwaitPointerEventScopes")
@Composable
fun AudioRecordPlayer(
    modifier: Modifier = Modifier,
    maxRecordTime: Long,
    currentRecordTime: Long,
    recordWaveForm: List<Int>,
    onChangeRecordFile: (Long) -> Unit
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp.minus(28.dp)
    val lazyListState = rememberLazyListState()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = colorResource(id = R.color.light_gray),
                shape = MaterialTheme.shapes.large
            )
    ) {
        LazyRow(
            modifier = Modifier.fillMaxSize(),
            state = lazyListState,
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Box(modifier = Modifier.width(maxWidth.div(2)))
            }
            items(recordWaveForm.size) { index ->
                val itemOffset =
                    lazyListState.layoutInfo.visibleItemsInfo.find { it.index == index }?.offset
                        ?: 0
                val screenWidthPx = with(LocalDensity.current) { maxWidth.toPx() }
                val centerX = screenWidthPx / 2

                val color = if ((itemOffset + 4.dp.value) < centerX) {
                    MaterialTheme.colors.secondary
                } else {
                    MaterialTheme.colors.secondary.copy(0.2f)
                }

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(8.dp)
                        .background(
                            color = color,
                            shape = CircleShape
                        )
                )
                if (index < recordWaveForm.size - 1) {
                    Spacer(modifier = Modifier.width(2.dp))
                }
            }
            item {
                Box(modifier = Modifier.width(maxWidth.div(2)))
            }
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width / 2 - 4.dp.toPx(), 0f)
                lineTo(size.width / 2 + 4.dp.toPx(), 0f)
                lineTo(size.width / 2, 12.dp.toPx())
                close()
            }

            drawPath(
                path = path,
                color = Color.Red
            )
            val centerX = size.width / 2
            drawLine(
                color = Color.Red,
                start = Offset(centerX, 14.dp.toPx()),
                end = Offset(centerX, size.height),
                strokeWidth = 2.dp.toPx()
            )
        }
    }
}

@Preview
@Composable
private fun TestPreview() {
    NeedTalkTheme {
        AudioRecordPlayer(
            maxRecordTime = 1000,
            currentRecordTime = 0L,
            recordWaveForm = (0..500).toList(),
            onChangeRecordFile = {}
        )
    }
}

@Composable
fun AudioRecordButtons(
    modifier: Modifier = Modifier,
    isPlaying: Boolean
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = CircleShape
                    )
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "-5s",
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = CircleShape
                    )
                    .size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = if (isPlaying) {
                        painterResource(id = R.drawable.ic_pause)
                    } else {
                        painterResource(id = R.drawable.ic_play)
                    },
                    contentDescription = "Resume",
                    tint = MaterialTheme.colors.onSecondary
                )
            }
            Spacer(modifier = Modifier.width(20.dp))
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        color = colorResource(id = R.color.light_gray),
                        shape = CircleShape
                    )
                    .size(48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "+5s",
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
        Row {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "Clips",
                    tint = MaterialTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "클립 목록",
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
            }
            Spacer(modifier = Modifier.width(32.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    modifier = Modifier.size(28.dp),
                    painter = painterResource(id = R.drawable.ic_cut),
                    contentDescription = "Clips",
                    tint = MaterialTheme.colors.onPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "클립 생성",
                    style = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
            }
        }
    }
}

//@Preview
//@Composable
//private fun AuioPlayerUIPreview() {
//    NeedTalkTheme {
//        AudioPlayerUI()
//    }
//}

@Composable
fun AudioPlayerUI() {
    var currentTime by remember { mutableStateOf(0f) }
    val totalTime = 200f // 총 녹음 시간 (예: 200초)

    LaunchedEffect(Unit) {
        while (currentTime < totalTime) {
            delay(100)
            currentTime += 0.1f
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatTime(currentTime.toInt()),
            fontSize = 36.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatTime(totalTime.toInt()),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        WaveformSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            progress = currentTime / totalTime
        )
    }
}

@Composable
fun WaveformSlider(
    modifier: Modifier = Modifier,
    progress: Float
) {
    var offsetX by remember { mutableStateOf(0f) }
    val waveformWidth = 2000f // 파형의 전체 너비를 설정합니다.
    val maxOffsetX = waveformWidth / 2 - 500f // 스크롤 가능한 최대 오프셋 (왼쪽 제한)
    val minOffsetX = -waveformWidth / 2 + 500f // 스크롤 가능한 최소 오프셋 (오른쪽 제한)

    // 자동으로 offsetX를 업데이트하는 애니메이션
//    val animatedOffsetX by animateFloatAsState(
//        targetValue = offsetX - 5f,
//        animationSpec = infiniteRepeatable(
//            animation = tween(
//                durationMillis = 100, // 속도 조절
//                easing = LinearEasing
//            )
//        ), label = ""
//    )
//
//    LaunchedEffect(progress) {
//        offsetX = animatedOffsetX.coerceIn(minOffsetX, maxOffsetX)
//    }

    Box(modifier = modifier.pointerInput(Unit) {
        detectHorizontalDragGestures { change, dragAmount ->
            offsetX = (offsetX + dragAmount).coerceIn(minOffsetX, maxOffsetX)
            change.consume()
        }
    }) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val barCount = 50
            val barWidth = size.width / barCount
            for (i in 0 until barCount) {
                val x = i * barWidth + offsetX
                if (x in 0f..size.width) {
                    val y = size.height * (0.1f + 0.8f * (i % 2))
                    drawLine(
                        color = if (i < progress * barCount) Color.Gray else Color.LightGray,
                        start = Offset(x, size.height),
                        end = Offset(x, y),
                        strokeWidth = barWidth - 4.dp.toPx(),
                        cap = StrokeCap.Round
                    )
                }
            }

            // 가운데 빨간 선 그리기
            val centerX = size.width / 2
            drawLine(
                color = Color.Red,
                start = Offset(centerX, 0f),
                end = Offset(centerX, size.height),
                strokeWidth = 4.dp.toPx(),
                cap = StrokeCap.Round
            )
        }
    }
}

// TODO: 나중에 구현
@Composable
fun FileInfoDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    talkHistory: TalkHistory?,
    recordFile: File
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(modifier = modifier.fillMaxWidth()) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "파일 정보",
                    style = MaterialTheme.typography.h5.copy(
                        color = MaterialTheme.colors.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    modifier = Modifier
                        .size(20.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterEnd),
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            InfoText(
                hint = "파일 제목",
                text = recordFile.name
            )
            Spacer(modifier = Modifier.height(20.dp))
            InfoText(
                hint = "파일 크기",
                text = getFileSizeToStr(talkHistory?.recordFile?.length() ?: 0L)
            )
            Spacer(modifier = Modifier.height(20.dp))
            InfoText(
                hint = "파일 경로",
                text = recordFile.absolutePath
            )
            Spacer(modifier = Modifier.height(20.dp))
            InfoText(
                hint = "재생 시간",
                text = SimpleDateFormat(
                    "HH:mm:ss",
                    Locale.KOREA
                ).format((talkHistory?.talkTime ?: 0L).minus(32400000))
            )
            Spacer(modifier = Modifier.height(20.dp))
            InfoText(
                hint = "생성 날짜",
                text = SimpleDateFormat(
                    "yyyy년 MM월 dd일",
                    Locale.KOREA
                ).format((talkHistory?.createTimeStamp ?: 0L))
            )
        }
    }
}

@Composable
fun RenameTitleDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    title: String,
    onTitleChange: (String) -> Unit
) {
    SimpleInputDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        title = "제목 수정",
        hint = "대화 제목",
        startInputData = title,
        maxLength = 30,
        buttonText = "수정하기",
        onButtonClick = onTitleChange
    )
}

@Composable
fun RemoveTalkHistoryDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onClickRemove: () -> Unit
) {
    ConfirmWithCancelDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        title = "대화기록 삭제",
        message = "기록된 녹음 파일도 모두 삭제됩니다.\n대화기록을 삭제하시겠습니까?",
        confirmText = "삭제하기",
        cancelText = "취소",
        onConfirm = { onClickRemove() }
    )
}

@Composable
fun InfoText(
    modifier: Modifier = Modifier,
    hint: String,
    text: String
) {
    Column(modifier = modifier) {
        Text(
            text = hint,
            style = MaterialTheme.typography.body1
                .copy(
                    color = colorResource(id = R.color.gray)
                )
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.h5
                .copy(color = MaterialTheme.colors.onPrimary)
        )
    }
}

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}