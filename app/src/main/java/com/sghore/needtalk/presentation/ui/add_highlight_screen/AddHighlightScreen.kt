package com.sghore.needtalk.presentation.ui.add_highlight_screen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sghore.needtalk.R
import com.sghore.needtalk.presentation.ui.BaselineTextField
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun AddHighlightScreen(
    uiState: AddHighlightUiState,
    onEvent: (AddHighlightUiEvent) -> Unit
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (toolbar, topLayout, subMidLayout, midLayout, midText, bottomLayout) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(toolbar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                }
                .padding(start = 14.dp, end = 14.dp)
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .clickable { onEvent(AddHighlightUiEvent.ClickNavigateUp) },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "navigateBack",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Box(
            modifier = Modifier
                .constrainAs(topLayout) {
                    top.linkTo(toolbar.bottom)
                    bottom.linkTo(subMidLayout.top)
                }
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            BaselineTextField(
                hint = "제목을 지정해주세요.",
                text = uiState.title,
                maxTextLength = 20,
                maxLine = 2,
                onValueChange = { onEvent(AddHighlightUiEvent.ChangeTitle(it)) }
            )
        }

        if (uiState.recordFile != null) {
            AudioRecordTime(
                modifier = Modifier.constrainAs(subMidLayout) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(midLayout.top, 12.dp)
                },
                maxRecordTime = uiState.playerMaxTime,
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
                maxRecordTime = uiState.playerMaxTime,
                startRecordTime = uiState.cutStartTime,
                endRecordTime = uiState.cutEndTime,
                recordWaveForm = uiState.recordAmplitude,
                isPlaying = uiState.isPlaying,
                onChangeTime = { startTime, endTime ->
                    onEvent(AddHighlightUiEvent.ChangePlayerTime(startTime, endTime))
                }
            )
            Text(
                modifier = Modifier.constrainAs(midText) {
                    top.linkTo(midLayout.bottom, 8.dp)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                text = SimpleDateFormat("mm:ss", Locale.KOREA)
                    .format((uiState.cutEndTime - uiState.cutStartTime)),
                style = MaterialTheme.typography.body1.copy(
                    color = colorResource(id = R.color.gray)
                )
            )
            AudioRecordButtons(
                modifier = Modifier.constrainAs(bottomLayout) {
                    top.linkTo(midLayout.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
                isPlaying = uiState.isPlaying,
                isCompleteEnable = uiState.title.isNotEmpty(),
                onClickPlay = {
                    onEvent(AddHighlightUiEvent.ClickPlayOrPause(it))
                },
                onClickComplete = { onEvent(AddHighlightUiEvent.ClickComplete) }
            )
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

@Composable
fun AudioRecordPlayer(
    modifier: Modifier = Modifier,
    currentRecordTime: Long,
    maxRecordTime: Long,
    startRecordTime: Long,
    endRecordTime: Long,
    recordWaveForm: List<Int>,
    isPlaying: Boolean,
    onChangeTime: (Long, Long) -> Unit
) {
    val localDensity = LocalDensity.current

    val maxWidth = LocalConfiguration.current.screenWidthDp.dp.minus(28.dp)
    val maxWidthPx = with(localDensity) { maxWidth.toPx() }.toInt()
    val listMaxWidth = recordWaveForm.size.dp.times(4).minus(2.dp)
    val listMaxWidthPx = with(localDensity) { listMaxWidth.toPx() }.toInt()
    var cutWidthPx by remember {
        mutableFloatStateOf(((endRecordTime - startRecordTime).toFloat() / maxRecordTime * listMaxWidthPx))
    }

    val lazyListState = rememberLazyListState()
    var playerOffset by remember { mutableIntStateOf(0) }
    var cutStartOffset by remember { mutableFloatStateOf(0f) }
    var cutEndOffset by remember { mutableFloatStateOf(cutWidthPx) }
    val cutStartOffsetDp = with(localDensity) { cutStartOffset.toDp() }

    LaunchedEffect(lazyListState) {
        snapshotFlow { lazyListState.firstVisibleItemScrollOffset to lazyListState.firstVisibleItemIndex }
            .collect { (offset, index) ->
                playerOffset = (12 * (index)) + offset

                val currentTime =
                    (maxRecordTime.toFloat() / listMaxWidthPx * (playerOffset + cutStartOffset)).toLong()
                val endTime =
                    (maxRecordTime.toFloat() / listMaxWidthPx * (playerOffset + cutEndOffset)).toLong()
                onChangeTime(currentTime, endTime)
            }
    }

    if (isPlaying) {
//        LaunchedEffect(currentRecordTime) {
//            val offset = (currentRecordTime.toFloat() / maxRecordTime * listMaxWidthPx)
//            lazyListState.scrollBy(offset - currentPlayerOffset)
//        }
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
                .padding(14.dp),
            state = lazyListState,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(recordWaveForm.size) { index ->
                val amplitude = recordWaveForm[index].toFloat() / 32767
                val amplitudeHeight = 8.dp + (120.dp - 8.dp) * amplitude

                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(amplitudeHeight)
                        .background(
                            color = MaterialTheme.colors.secondary,
                            shape = CircleShape
                        )
                )
                if (index < recordWaveForm.size - 1) {
                    Spacer(modifier = Modifier.width(2.dp))
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(start = 2.dp, end = 2.dp)
                .offset(x = cutStartOffsetDp),
            verticalAlignment = Alignment.Bottom
        ) {
            val currentOffset =
                ((currentRecordTime - startRecordTime).toFloat() / maxRecordTime * listMaxWidthPx)
            val cutWidth = with(localDensity) { cutWidthPx.toDp() }
            val rectColor = MaterialTheme.colors.secondary.copy(alpha = 0.2f)
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(42.dp)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                    )
                    .pointerInput(Unit) {
                        // low: 117.1145, high: 702.68695
                        detectHorizontalDragGestures { change, dragAmount ->
                            cutStartOffset += dragAmount

                            cutStartOffset = cutStartOffset
                                .coerceIn(cutEndOffset - 702.68695f, cutEndOffset - 117.1145f)
                            cutWidthPx = (cutEndOffset - cutStartOffset)

                            val startTime =
                                (maxRecordTime.toFloat() / listMaxWidthPx * (cutStartOffset + playerOffset)).toLong()
                            val endTime =
                                (maxRecordTime.toFloat() / listMaxWidthPx * (cutEndOffset + playerOffset)).toLong()

                            onChangeTime(startTime, endTime)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(26.dp)
                        .background(
                            color = MaterialTheme.colors.onSecondary,
                            shape = CircleShape
                        )
                )
            }
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(cutWidth)
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            cutStartOffset += dragAmount
                            cutEndOffset += dragAmount

                            cutStartOffset = cutStartOffset.coerceIn(0f, maxWidthPx - cutWidthPx)
                            cutEndOffset = cutEndOffset.coerceIn(cutWidthPx, maxWidthPx.toFloat())

                            val startTime =
                                (maxRecordTime.toFloat() / listMaxWidthPx * (cutStartOffset + playerOffset)).toLong()
                            val endTime =
                                (maxRecordTime.toFloat() / listMaxWidthPx * (cutEndOffset + playerOffset)).toLong()
                            onChangeTime(startTime, endTime)
                        }
                    }
            ) {
                val path = Path().apply {
                    moveTo(currentOffset + -4.dp.toPx(), 0f)
                    lineTo(currentOffset + 4.dp.toPx(), 0f)
                    lineTo(currentOffset, 12.dp.toPx())
                    close()
                }

                drawPath(
                    path = path,
                    color = Color.Red
                )
                drawLine(
                    color = Color.Red,
                    start = Offset(currentOffset, 14.dp.toPx()),
                    end = Offset(currentOffset, size.height),
                    strokeWidth = 2.dp.toPx()
                )
                drawRect(
                    color = rectColor,
                    size = Size(
                        width = size.width,
                        height = size.height
                    )
                )
            }
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(42.dp)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                    )
                    .pointerInput(Unit) {
                        // low: 117.1145, high: 702.68695
                        detectHorizontalDragGestures { change, dragAmount ->
                            cutEndOffset += dragAmount
                            cutEndOffset = cutEndOffset
                                .coerceIn(cutStartOffset + 117.1145f, cutStartOffset + 702.68695f)
                            cutWidthPx = (cutEndOffset - cutStartOffset)

                            val startTime =
                                (maxRecordTime.toFloat() / listMaxWidthPx * (cutStartOffset + playerOffset)).toLong()
                            val endTime =
                                (maxRecordTime.toFloat() / listMaxWidthPx * (cutEndOffset + playerOffset)).toLong()
                            onChangeTime(startTime, endTime)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(26.dp)
                        .background(
                            color = MaterialTheme.colors.onSecondary,
                            shape = CircleShape
                        )
                )
            }
        }
    }
}

@Composable
fun AudioRecordButtons(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
    isCompleteEnable: Boolean,
    onClickPlay: (Boolean) -> Unit,
    onClickComplete: () -> Unit,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
        Spacer(modifier = Modifier.height(32.dp))
        Column(
            modifier = if (isCompleteEnable) {
                Modifier.clickable { onClickComplete() }
            } else {
                Modifier.alpha(0.4f)
            },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                modifier = Modifier.size(28.dp),
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "Complete",
                tint = MaterialTheme.colors.onPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "생성하기",
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onPrimary
                )
            )
        }
    }
}