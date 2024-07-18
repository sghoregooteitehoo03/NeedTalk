package com.sghore.needtalk.presentation.ui.add_highlight_screen

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    uiState: AddHighlightUiState
) {
    ConstraintLayout(modifier = Modifier.fillMaxSize()) {
        val (toolbar, topLayout, subMidLayout, midLayout, bottomLayout) = createRefs()
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
                    .clickable { },
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
                onValueChange = { }
            )
        }

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
                }
                .padding(start = 14.dp, end = 14.dp),
            currentRecordTime = uiState.playerTime,
            maxRecordTime = uiState.playerMaxTime,
            startRecordTime = uiState.cutStartTime,
            endRecordTime = uiState.cutEndTime,
            recordWaveForm = uiState.recordAmplitude,
            isPlaying = uiState.isPlaying,
            onChangeTime = {},
            onSeeking = {}
        )
        AudioRecordButtons(
            modifier = Modifier.constrainAs(bottomLayout) {
                top.linkTo(midLayout.bottom)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            },
            isPlaying = uiState.isPlaying,
            onClickPlay = {},
            onClickComplete = {}
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
    startRecordTime: Long,
    endRecordTime: Long,
    recordWaveForm: List<Int>,
    isPlaying: Boolean,
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
                currentOffset = (12 * (index)) + offset

//                val currentTime =
//                    (maxRecordTime.toFloat() / listMaxWidthPx * currentOffset).toLong()
//                onChangeTime(currentTime)
            }
    }

    if (isPlaying) {
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
        Row(
            modifier = Modifier.padding(start = 6.dp, end = 6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            val cutWidthPx =
                ((endRecordTime - startRecordTime).toFloat() / maxRecordTime * listMaxWidthPx)
            val cutWidth = with(localDensity) { cutWidthPx.toDp() }
            val rectColor = MaterialTheme.colors.secondary.copy(alpha = 0.2f)
            Box(
                modifier = Modifier
                    .width(8.dp)
                    .height(36.dp)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(topStart = 4.dp, bottomStart = 4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(20.dp)
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
            ) {
                val path = Path().apply {
                    moveTo(-4.dp.toPx(), 0f)
                    lineTo(4.dp.toPx(), 0f)
                    lineTo(0f, 12.dp.toPx())
                    close()
                }

                drawPath(
                    path = path,
                    color = Color.Red
                )
                val centerX = size.width / 2
                drawLine(
                    color = Color.Red,
                    start = Offset(0f, 14.dp.toPx()),
                    end = Offset(0f, size.height),
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
                    .width(8.dp)
                    .height(36.dp)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .height(20.dp)
                        .background(
                            color = MaterialTheme.colors.onSecondary,
                            shape = CircleShape
                        )
                )
            }
        }
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
    }
}

@Composable
fun AudioRecordButtons(
    modifier: Modifier = Modifier,
    isPlaying: Boolean,
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
            modifier = Modifier.clickable { onClickComplete() },
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