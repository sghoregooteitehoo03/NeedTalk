package com.sghore.needtalk.presentation.ui.timer_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.presentation.ui.NameTag
import com.sghore.needtalk.presentation.ui.RoundedButton
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.presentation.ui.theme.Orange50
import com.sghore.needtalk.presentation.ui.theme.Orange80
import com.sghore.needtalk.util.calcDominantColor
import com.sghore.needtalk.util.parseMinuteSecond

@Composable
fun TimerScreen(
    uiState: TimerUiState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp),
            contentAlignment = Alignment.Center
        ) {
            val musicInfo = uiState.timerInfo?.musicInfo
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

            GroupMember(
                modifier = Modifier.constrainAs(groupMember) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
                currentUser = uiState.userEntity,
                userList = uiState.timerInfo?.userList ?: listOf(),
                maxMember = uiState.timerInfo?.maxMember ?: 0
            )
            TimerContent(
                modifier = Modifier.constrainAs(timer) {
                    top.linkTo(groupMember.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(explainText.bottom)
                },
                currentTime = uiState.currentTime,
                maxTime = uiState.timerInfo?.timerTime ?: 0L,
            )
            Text(
                modifier = Modifier.constrainAs(explainText) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(btnLayout.top, margin = 46.dp)
                },
                text = "멤버가 모두 들어올 때 까지\n잠시 기다려주세요.",
                style = MaterialTheme.typography.h5.copy(
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center
                )
            )
            Row(modifier = Modifier.constrainAs(btnLayout) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, margin = 32.dp)
            }) {
                RoundedButton(
                    modifier = Modifier.width(110.dp),
                    text = "나가기",
                    color = colorResource(id = R.color.light_gray_200),
                    textStyle = MaterialTheme.typography.body1.copy(color = Color.White),
                    paddingValues = PaddingValues(14.dp),
                    onClick = {}
                )
                Spacer(modifier = Modifier.width(20.dp))
                RoundedButton(
                    modifier = Modifier.width(110.dp),
                    text = "시작하기",
                    color = MaterialTheme.colors.secondary,
                    textStyle = MaterialTheme.typography.body1.copy(color = Color.White),
                    paddingValues = PaddingValues(14.dp),
                    onClick = {}
                )
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
    val defaultColor = MaterialTheme.colors.surface
    var dominantColor by remember {
        mutableStateOf(defaultColor)
    }
    val painter = rememberAsyncImagePainter(
        model = thumbnailImage,
        onSuccess = { result ->
            calcDominantColor(result.result.drawable) { color ->
                dominantColor = color
            }
        }
    )

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier
                .size(26.dp)
                .clip(RoundedCornerShape(8.dp)),
            painter = painter,
            contentDescription = thumbnailImage,
            contentScale = ContentScale.Crop
        )
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
    userList: List<UserEntity>,
    maxMember: Int
) {
    Row(modifier = modifier) {
        repeat(userList.size) { index ->
            val user = userList[index]
            Spacer(modifier = Modifier.width(9.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NameTag(
                    name = user.name,
                    color = Color(user.color),
                    interval = 4.dp,
                    colorSize = 10.dp,
                    textStyle = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onPrimary,
                        fontWeight = if (user.userId == (currentUser?.userId ?: "")) {
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
                        )
                )
            }
            Spacer(modifier = Modifier.width(9.dp))
        }
        repeat(maxMember - userList.size) {
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

@Composable
fun TimerContent(
    modifier: Modifier = Modifier,
    currentTime: Long,
    maxTime: Long,
) {
    val isStopwatch = remember { maxTime == -1L }
    val maxWidth = LocalConfiguration.current.screenWidthDp
    val progress = if (isStopwatch) {
        1f
    } else {
        if ((currentTime / maxTime.toFloat()) < 0.025f) {
            (currentTime / maxTime.toFloat()) + 0.025f
        } else {
            (currentTime / maxTime.toFloat())
        }
    }

    Column(
        modifier = modifier
            .padding(start = 14.dp, end = 14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = parseMinuteSecond(timeStamp = currentTime),
            style = MaterialTheme.typography.h1.copy(Orange50)
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
                                        color = if (isStopwatch) {
                                            Orange80
                                        } else {
                                            Orange50
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

@Preview
@Composable
fun GroupMemberPreview() {
    NeedTalkTheme {
        val testUserList = listOf(
            UserEntity(
                userId = "abc",
                name = "아령하세요",
                color = Color.Blue.toArgb()
            ),
            UserEntity(
                userId = "idna",
                name = "하이하이",
                color = Color.Magenta.toArgb()
            )
        )
        GroupMember(
            currentUser = testUserList[1],
            userList = testUserList,
            maxMember = 4
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
            maxTime = 3600000
        )
    }
}

