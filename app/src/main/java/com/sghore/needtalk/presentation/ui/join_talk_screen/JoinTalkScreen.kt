package com.sghore.needtalk.presentation.ui.join_talk_screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TimerInfo
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.ProfileImage
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.util.byteArrayToBitmap
import com.sghore.needtalk.util.parseMinuteSecond
import kotlinx.coroutines.launch

@Composable
fun JoinTalkScreen(
    uiState: JoinUiState,
    onEvent: (JoinTalkUiEvent) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colors.secondary)
    ) {
        val (toolbar, layout, adview) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(toolbar) {
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
                .height(54.dp)
                .background(
                    color = MaterialTheme.colors.secondary
                )
                .padding(start = 14.dp, end = 14.dp)
        ) {
            Icon(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .clip(CircleShape)
                    .size(24.dp)
                    .clickable { onEvent(JoinTalkUiEvent.ClickBackArrow) },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "NavigateUp",
                tint = MaterialTheme.colors.onSecondary
            )
            if (uiState.searchNearDevice is SearchNearDevice.Load) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clip(CircleShape)
                        .size(24.dp)
                        .clickable { onEvent(JoinTalkUiEvent.ClickResearch) },
                    painter = painterResource(id = R.drawable.ic_refresh),
                    contentDescription = "Refresh",
                    tint = MaterialTheme.colors.onSecondary
                )
            }
        }

//        AdmobBanner(modifier = Modifier.constrainAs(adview) {
//            bottom.linkTo(parent.bottom)
//        })

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(layout) {
                    top.linkTo(toolbar.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
        ) {
            when (uiState.searchNearDevice) {
                is SearchNearDevice.Searching -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FoundingNearDevice(
                            modifier = Modifier.padding(14.dp),
                            isFound = uiState.searchNearDevice.isFound
                        )
                        Spacer(modifier = Modifier.height(42.dp))
                        Text(
                            text = "근처 대화방을 찾는 중이에요…",
                            style = MaterialTheme.typography.h4.copy(
                                color = MaterialTheme.colors.onSecondary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                is SearchNearDevice.Load -> {
                    val heightPixel = with(LocalDensity.current) {
                        LocalConfiguration.current.screenHeightDp.dp.toPx()
                    }
                    val offsetAnim = remember { Animatable(heightPixel) }

                    LaunchedEffect(uiState.searchNearDevice) {
                        offsetAnim.animateTo(
                            targetValue = 0f,
                            animationSpec = tween(800)
                        )
                    }

                    TimerInfoPager(
                        modifier = Modifier.offset {
                            IntOffset(
                                0,
                                offsetAnim.value.toInt()
                            )
                        },
                        timerInfoList = uiState.searchNearDevice.timerInfoList,
                        loadTimerInfo = { onEvent(JoinTalkUiEvent.LoadTimerInfo(it)) },
                        onClickJoin = { onEvent(JoinTalkUiEvent.ClickJoin(it)) }
                    )
                }
            }
        }
    }
}

@Composable
fun FoundingNearDevice(
    modifier: Modifier = Modifier,
    isFound: Boolean
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp - 56.dp
    val scaleAnim1 = remember { Animatable(1f) }
    val scaleAnim2 = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        launch {
            scaleAnim1.animateTo(
                targetValue = 1.18f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
        launch {
            scaleAnim2.animateTo(
                targetValue = 1.12f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
        }
    }

    Box(contentAlignment = Alignment.Center) {
        Box(
            modifier = modifier
                .width(maxWidth)
                .height(maxWidth)
                .scale(scaleAnim1.value)
                .clip(CircleShape)
                .background(color = Color.White.copy(alpha = 0.2f))
        )
        Box(
            modifier = Modifier
                .width(maxWidth - 80.dp)
                .height(maxWidth - 80.dp)
                .scale(scaleAnim2.value)
                .clip(CircleShape)
                .background(color = Color.White.copy(alpha = 0.3f))
        )
        Box(
            modifier = Modifier
                .width(maxWidth - 160.dp)
                .height(maxWidth - 160.dp)
                .clip(CircleShape)
                .background(color = Color.White.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Crossfade(targetState = isFound, label = "", animationSpec = tween(300)) {
                val iconResource = if (it) {
                    R.drawable.ic_check
                } else {
                    R.drawable.ic_search
                }

                Icon(
                    modifier = Modifier
                        .width(maxWidth - 200.dp)
                        .height(maxWidth - 200.dp),
                    painter = painterResource(id = iconResource),
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimerInfoPager(
    modifier: Modifier = Modifier,
    timerInfoList: List<TimerInfo?>,
    loadTimerInfo: (index: Int) -> Unit,
    onClickJoin: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { timerInfoList.size })
    val currentPage = pagerState.currentPage

    LaunchedEffect(
        key1 = currentPage,
        block = {
            if (timerInfoList[currentPage] == null) {
                loadTimerInfo(currentPage) // 타이머 정보를 가져옴
            }
        }
    )

    ConstraintLayout(
        modifier = modifier
            .fillMaxSize()
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
            )
            .padding(14.dp)
    ) {
        val (pageDot, loading, mainLayout, host, button) = createRefs()
        Row(
            modifier = Modifier.constrainAs(pageDot) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(parent.top)
            }
        ) {
            repeat(timerInfoList.size) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .then(
                            if (currentPage == it) {
                                Modifier.background(
                                    color = MaterialTheme.colors.secondary,
                                    shape = CircleShape
                                )
                            } else {
                                Modifier.border(
                                    width = 1.dp,
                                    color = MaterialTheme.colors.secondary,
                                    shape = CircleShape
                                )
                            }
                        )

                )
                if (it != timerInfoList.size - 1) {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
        HorizontalPager(
            modifier = Modifier
                .constrainAs(mainLayout) {
                    top.linkTo(pageDot.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(button.top)
                    height = Dimension.fillToConstraints
                },
            state = pagerState,
            userScrollEnabled = timerInfoList[currentPage] != null
        ) { index ->
            if (timerInfoList[index] != null) {
                TimerInfoItem(timerInfo = timerInfoList[index]!!)
            }
        }

        if (timerInfoList[currentPage] != null) {
            Row(
                modifier = Modifier.constrainAs(host) {
                    start.linkTo(parent.start)
                    bottom.linkTo(button.top, 12.dp)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val hostInfo = timerInfoList[currentPage]!!.participantInfoList[0]!!
                ProfileImage(
                    backgroundSize = 36.dp,
                    imageSize = 26.dp,
                    profileImage = byteArrayToBitmap(hostInfo.profileImage).asImageBitmap()
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = hostInfo.name,
                    style = MaterialTheme.typography.h5.copy(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
            }
        } else {
            CircularProgressIndicator(
                modifier = Modifier.constrainAs(loading) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    top.linkTo(pageDot.bottom)
                    bottom.linkTo(button.top)
                },
                color = MaterialTheme.colors.onPrimary
            )
        }

        DefaultButton(
            modifier = Modifier.constrainAs(button) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom, 10.dp)
            },
            text = "참가하기",
            isEnabled = timerInfoList[currentPage] != null,
            onClick = { onClickJoin(timerInfoList[currentPage]!!.hostEndpointId) }
        )
    }
}

@Composable
fun TimerInfoItem(
    modifier: Modifier = Modifier,
    timerInfo: TimerInfo
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TimerInfoLayout(title = "대화 시간") {
            Text(
                text = parseMinuteSecond(timerInfo.timerTime),
                style = MaterialTheme.typography.h2.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 40.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        TimerInfoLayout(title = "인원 (${timerInfo.participantInfoList.size}/${timerInfo.maxMember})") {
            Column {
                Spacer(modifier = Modifier.height(10.dp))
                Row {
                    repeat(timerInfo.participantInfoList.size) {
                        ProfileImage(
                            backgroundSize = 42.dp,
                            imageSize = 32.dp,
                            profileImage = byteArrayToBitmap(timerInfo.participantInfoList[it]!!.profileImage).asImageBitmap()
                        )
                        if (it != timerInfo.participantInfoList.size - 1) {
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        TimerInfoLayout(title = "상태") {
            Text(
                text = if (timerInfo.isStart) {
                    "대화 시작 됨"
                } else {
                    "대기 중"
                },
                style = MaterialTheme.typography.h2.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 40.sp
                )
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Row {
            OptionLayout(
                text = if (timerInfo.timerTime > 0) {
                    "타이머"
                } else {
                    "스톱워치"
                },
                icon = if (timerInfo.timerTime > 0) {
                    painterResource(id = R.drawable.ic_timer)
                } else {
                    painterResource(id = R.drawable.ic_timer_off)
                }
            )
            Spacer(modifier = Modifier.width(16.dp))
            OptionLayout(
                text = if (timerInfo.isAllowMic) {
                    "녹음 허용"
                } else {
                    "녹음 비허용"
                },
                icon = if (timerInfo.isAllowMic) {
                    painterResource(id = R.drawable.ic_mic)
                } else {
                    painterResource(id = R.drawable.ic_mic_off)
                }
            )
        }
    }
}

@Composable
fun TimerInfoLayout(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = modifier
            .shadow(2.dp, shape = MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .fillMaxWidth()
            .padding(14.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.h5.copy(
                color = colorResource(id = R.color.gray)
            )
        )
        content()
    }
}

@Composable
fun OptionLayout(
    modifier: Modifier = Modifier,
    text: String,
    icon: Painter
) {
    Row(
        modifier = modifier
            .background(
                color = colorResource(id = R.color.light_gray),
                shape = MaterialTheme.shapes.medium
            )
            .padding(top = 6.dp, bottom = 6.dp, start = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(24.dp),
            painter = icon,
            contentDescription = "iconImage",
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.body1.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
    }
}

@Preview
@Composable
fun FoundingNearDevicePreview() {
    NeedTalkTheme {
        FoundingNearDevice(
            modifier = Modifier,
            isFound = false
        )
    }
}