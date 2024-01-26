package com.sghore.needtalk.presentation.ui.join_screen

import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.sghore.needtalk.R
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TimerInfo
import com.sghore.needtalk.presentation.ui.NameTag
import com.sghore.needtalk.presentation.ui.RoundedButton
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.util.parseMinuteSecond

// TODO:
//  . 재연결 UI 구현

@Composable
fun JoinScreen(
    uiState: JoinUiState,
    onEvent: (JoinUiEvent) -> Unit
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxSize()
    ) {
        val (toolbar, layout) = createRefs()
        Box(
            modifier = Modifier
                .constrainAs(toolbar) {
                    top.linkTo(parent.top)
                }
                .fillMaxWidth()
                .height(54.dp)
                .padding(start = 14.dp, end = 14.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Icon(
                modifier = Modifier
                    .clip(CircleShape)
                    .size(24.dp)
                    .clickable {
                        onEvent(JoinUiEvent.ClickBackArrow)
                    },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "NavigateUp",
                tint = MaterialTheme.colors.onPrimary
            )
        }

        Column(
            modifier = Modifier
                .constrainAs(layout) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            when (uiState.searchNearDevice) {
                is SearchNearDevice.Searching -> {
                    FoundingNearDevice(
                        modifier = Modifier.padding(14.dp),
                        isFound = uiState.searchNearDevice.isFound
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = "근처에 있는 사용자를 찾고있어요...",
                        style = MaterialTheme.typography.h4.copy(color = colorResource(id = R.color.gray))
                    )
                }

                is SearchNearDevice.Load -> {
                    TimerInfoPager(
                        timerInfoList = uiState.searchNearDevice.timerInfoList,
                        loadTimerInfo = { onEvent(JoinUiEvent.LoadTimerInfo(it)) },
                        onJoinClick = { onEvent(JoinUiEvent.ClickJoin(it)) }
                    )
                    Spacer(modifier = Modifier.height(28.dp))
                    Text(
                        text = "근처에 있는 사용자를 발견했어요!",
                        style = MaterialTheme.typography.h4.copy(color = colorResource(id = R.color.gray))
                    )
                }
            }
        }
    }
}

// TODO: 애니메이션 구현
@Composable
fun FoundingNearDevice(
    modifier: Modifier = Modifier,
    isFound: Boolean
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp - 56.dp
    Box(
        modifier = modifier
            .width(maxWidth)
            .height(maxWidth)
            .clip(CircleShape)
            .background(color = MaterialTheme.colors.secondary.copy(alpha = 0.2f)),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(maxWidth - 80.dp)
                .height(maxWidth - 80.dp)
                .clip(CircleShape)
                .background(color = MaterialTheme.colors.secondary.copy(alpha = 0.3f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(maxWidth - 160.dp)
                    .height(maxWidth - 160.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colors.secondary.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                val iconResource = if (isFound) {
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
    onJoinClick: (TimerInfo) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { timerInfoList.size })
    val currentPage = pagerState.currentPage
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp - 56.dp

    LaunchedEffect(
        key1 = currentPage,
        block = {
            if (timerInfoList[currentPage] == null) {
                loadTimerInfo(currentPage)
            }
        }
    )

    Box(
        modifier = Modifier
            .width(maxWidth)
            .height(maxWidth),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row {
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
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalPager(
                modifier = modifier.fillMaxWidth(),
                state = pagerState
            ) { index ->
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    TimerInfoItem(
                        modifier = Modifier.padding(14.dp),
                        timerInfo = timerInfoList[index],
                        onJoinClick = onJoinClick
                    )
                }
            }
        }
    }
}

@Composable
fun TimerInfoItem(
    modifier: Modifier = Modifier,
    timerInfo: TimerInfo?,
    onJoinClick: (TimerInfo) -> Unit
) {
    Box(
        modifier = Modifier
            .width(260.dp)
            .shadow(6.dp, RoundedCornerShape(20.dp))
            .background(
                color = MaterialTheme.colors.background,
                shape = RoundedCornerShape(20.dp)
            )
            .clip(RoundedCornerShape(20.dp))
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        if (timerInfo == null) {
            CircularProgressIndicator(
                modifier = Modifier.size(80.dp),
                color = MaterialTheme.colors.onPrimary
            )
        }

        Column(
            modifier = Modifier.alpha(if (timerInfo != null) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NameTag(
                name = timerInfo?.hostUser?.name ?: "",
                color = Color(timerInfo?.hostUser?.color ?: 0),
                interval = 6.dp,
                colorSize = 16.dp,
                textStyle = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = parseMinuteSecond(timerInfo?.timerTime ?: 0L),
                style = MaterialTheme.typography.h3.copy(
                    color = MaterialTheme.colors.onPrimary
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "대화 시간",
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onPrimary.copy(
                        alpha = 0.6f
                    )
                )
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(id = R.drawable.ic_people),
                    contentDescription = "People",
                    tint = colorResource(id = R.color.gray)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${timerInfo?.currentMember ?: 0}/${timerInfo?.maxMember ?: 0}",
                    style = MaterialTheme.typography.body1.copy(
                        color = colorResource(id = R.color.gray)
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            RoundedButton(
                modifier = Modifier.fillMaxWidth(),
                text = "참가하기",
                color = MaterialTheme.colors.secondary,
                textStyle = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onSecondary,
                    fontWeight = FontWeight.Medium
                ),
                paddingValues = PaddingValues(14.dp),
                enable = (timerInfo?.currentMember ?: 0) != (timerInfo?.maxMember ?: 0),
                onClick = { onJoinClick(timerInfo!!) }
            )
        }
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

@Preview
@Composable
fun TimerInfoPagerPreview() {
    NeedTalkTheme {
        val timerInfoList = listOf(
            TimerInfo(
                hostUser = UserEntity(
                    userId = "asdfasdf",
                    name = "방가방",
                    color = Color.Blue.toArgb()
                ),
                timerTime = 3520000,
                currentMember = 2,
                maxMember = 4,
            ),
            null,
            null
        )
        TimerInfoPager(
            timerInfoList = timerInfoList,
            loadTimerInfo = {},
            onJoinClick = {}
        )
    }
}