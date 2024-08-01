package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import com.sghore.needtalk.util.getFileSizeToStr
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
                        if (uiState.talkHistory?.recordFile != null) {
                            DropdownMenuItem(onClick = {
                                onEvent(TalkHistoryDetailUiEvent.OptionInfo)
                                isExpended = false
                            }) {
                                Text(
                                    text = "파일 정보",
                                    style = MaterialTheme.typography.body1.copy(
                                        color = MaterialTheme.colors.onPrimary
                                    )
                                )
                            }
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

        if (uiState.talkHistory?.recordFile != null) {
            if (uiState.talkHistory.recordAmplitude.isNotEmpty()) {
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
                        },
                    currentRecordTime = uiState.playerTime,
                    maxRecordTime = uiState.talkHistory.talkTime,
                    recordWaveForm = uiState.talkHistory.recordAmplitude,
                    isPlaying = uiState.isPlaying,
                    isJumping = uiState.isJumping,
                    onChangeTime = {
                        onEvent(TalkHistoryDetailUiEvent.ChangeTime(it))
                    },
                    onSeeking = {
                        onEvent(TalkHistoryDetailUiEvent.SeekPlayer(it))
                    }
                )

                AudioRecordButtons(
                    modifier = Modifier.constrainAs(bottomLayout) {
                        top.linkTo(midLayout.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                    isPlaying = uiState.isPlaying,
                    onClickBeforeSecond = { onEvent(TalkHistoryDetailUiEvent.ClickBeforeSecond) },
                    onClickAfterSecond = { onEvent(TalkHistoryDetailUiEvent.ClickAfterSecond) },
                    onClickPlay = { onEvent(TalkHistoryDetailUiEvent.ClickPlayOrPause(it)) },
                    onClickClipList = { onEvent(TalkHistoryDetailUiEvent.ClickClips) },
                    onClickMakeClip = { onEvent(TalkHistoryDetailUiEvent.ClickMakeClip) }
                )
            }
        }
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

@Composable
fun AudioRecordPlayer(
    modifier: Modifier = Modifier,
    currentRecordTime: Long,
    maxRecordTime: Long,
    recordWaveForm: List<Int>,
    isPlaying: Boolean,
    isJumping: Boolean,
    onChangeTime: (Long) -> Unit,
    onSeeking: (Boolean) -> Unit
) {
    val localDensity = LocalDensity.current

    val maxWidth = LocalConfiguration.current.screenWidthDp.dp.minus(28.dp)
    val listMaxWidth = recordWaveForm.size.dp.times(4).minus(2.dp)
    val halfWidthPx = with(localDensity) { maxWidth.div(2).toPx() }.toInt()
    val listMaxWidthPx = with(localDensity) { listMaxWidth.toPx() }.toInt()
    var currentOffset by remember { mutableIntStateOf(0) }

    val lazyListState = rememberLazyListState()

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset to lazyListState.firstVisibleItemIndex }
            .collect { (offset, index) ->
                currentOffset = if (index == 0) {
                    offset
                } else {
                    (12 * (index - 1)) + offset + halfWidthPx
                }

                val currentTime =
                    (maxRecordTime.toFloat() / listMaxWidthPx * currentOffset).toLong()
                onChangeTime(currentTime)
            }
    }

    if (isPlaying || isJumping) {
        LaunchedEffect(currentRecordTime) {
            val offset = (currentRecordTime.toFloat() / maxRecordTime * listMaxWidthPx)
            lazyListState.scrollBy(offset - currentOffset)
        }
    }

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
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onPress = { onSeeking(true) }
                    )
                },
            state = lazyListState,
            verticalAlignment = Alignment.CenterVertically
        ) {
            item {
                Box(modifier = Modifier.width(maxWidth.div(2)))
            }
            items(recordWaveForm.size) { index ->
                val itemOffset = remember(index) { 0 + 12 * index }

                val color = if (itemOffset < currentOffset) {
                    MaterialTheme.colors.secondary
                } else {
                    MaterialTheme.colors.secondary.copy(0.2f)
                }
                val amplitude = recordWaveForm[index].toFloat() / 32767
                val amplitudeHeight = 8.dp + (120.dp - 8.dp) * amplitude

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(amplitudeHeight)
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

@Composable
fun AudioRecordButtons(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    onClickBeforeSecond: () -> Unit,
    onClickAfterSecond: () -> Unit,
    onClickPlay: (Boolean) -> Unit,
    onClickClipList: () -> Unit,
    onClickMakeClip: () -> Unit
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
                    .size(48.dp)
                    .clickable { onClickBeforeSecond() },
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
                    .size(64.dp)
                    .clickable { onClickPlay(!isPlaying) },
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
                    .size(48.dp)
                    .clickable { onClickAfterSecond() },
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
            Column(
                modifier = Modifier.clickable { onClickClipList() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
            Column(
                modifier = Modifier.clickable { onClickMakeClip() },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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

@Composable
fun FileInfoDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    talkHistory: TalkHistory?
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        val recordFile = remember { talkHistory?.recordFile }
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
                        .align(Alignment.CenterEnd)
                        .clickable { onDismiss() },
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close",
                    tint = MaterialTheme.colors.onPrimary
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            InfoText(
                hint = "파일 제목",
                text = recordFile?.name ?: ""
            )
            Spacer(modifier = Modifier.height(20.dp))
            InfoText(
                hint = "파일 크기",
                text = getFileSizeToStr(talkHistory?.recordFile?.length() ?: 0L)
            )
            Spacer(modifier = Modifier.height(20.dp))
            InfoText(
                hint = "파일 경로",
                text = recordFile?.absolutePath ?: ""
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