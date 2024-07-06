package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.ProfileImage
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun TalkHistoryDetailScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Column {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(start = 14.dp, end = 14.dp),
            ) {
                val (navigateUp, title, more) = createRefs()
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .constrainAs(navigateUp) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
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
                }) {
                    Text(
                        text = "대화에 제목입니다.",
                        style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = SimpleDateFormat(
                            "yy.MM.dd (E)",
                            Locale.KOREA
                        ).format(System.currentTimeMillis()),
                        style = MaterialTheme.typography.subtitle1.copy(color = colorResource(id = R.color.gray))
                    )
                }
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .constrainAs(more) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        },
                    painter = painterResource(id = R.drawable.ic_more),
                    contentDescription = "More",
                    tint = MaterialTheme.colors.onPrimary
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
fun AudioRecordPlayer(
    modifier: Modifier = Modifier,
    maxRecordTime: Long,
    currentRecordTime: Long,
    onChangeRecordFile: (Long) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .background(
                color = colorResource(id = R.color.light_gray),
                shape = MaterialTheme.shapes.large
            )
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val path = Path().apply {
                moveTo(size.width / 2 - 4.dp.toPx(), 0f)
                lineTo(size.width / 2 + 4.dp.toPx(), 0f) // Bottom-left vertex
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
        AudioRecordPlayer(maxRecordTime = 0L, currentRecordTime = 0L, onChangeRecordFile = {})
    }
}

@Composable
fun AudioPlayerUI() {
    var currentTime by remember { mutableStateOf(5.28f) }
    val totalTime = 120f // 2:00:00 in seconds

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = formatTime(totalTime.toInt()),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = formatTime(currentTime.roundToInt()),
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        WaveformSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            onProgressChanged = { progress ->
                currentTime = progress * totalTime
            }
        )
    }
}

@Composable
fun WaveformSlider(
    modifier: Modifier = Modifier,
    onProgressChanged: (Float) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition()
    val offsetX by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ), label = ""
    )

    Box(modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // 파형을 시뮬레이션하는 예제
            val barCount = 100
            val barWidth = size.width / barCount
            for (i in 0 until barCount) {
                val x = ((i - offsetX * barCount) * barWidth) % size.width
                val y = size.height * (0.1f + 0.8f * (i % 2))
                drawLine(
                    color = if (x >= size.width / 2 - barWidth / 2 && x <= size.width / 2 + barWidth / 2) Color.Red else Color.Yellow,
                    start = Offset(x, size.height),
                    end = Offset(x, y),
                    strokeWidth = barWidth - 4.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }

            // 가운데 빨간 줄 그리기
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

fun formatTime(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}