@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class)

package com.sghore.needtalk.presentation.ui.create_talk_screen

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.EmptyTalkUserInfo
import com.sghore.needtalk.presentation.ui.TalkUserInfo

@Composable
fun CreateTalkScreen(
    userData: UserData?,
    uiState: CreateTalkUiState,
    onEvent: (CreateTalkUiEvent) -> Unit
) {
    Column(modifier = Modifier.background(MaterialTheme.colors.secondary)) {
        Box(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp)
                .fillMaxWidth()
                .height(56.dp)
                .background(color = MaterialTheme.colors.secondary),
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterStart)
                    .clickable { onEvent(CreateTalkUiEvent.ClickBackArrow) },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "navigateUp",
                tint = MaterialTheme.colors.onSecondary
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterEnd)
                    .clickable { },
                painter = painterResource(id = R.drawable.ic_check),
                contentDescription = "navigateUp",
                tint = MaterialTheme.colors.onSecondary
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.28f)
                .padding(14.dp)
        ) {
            SetTalkTimeLayout()
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .padding(10.dp)
        ) {
            TalkOptionsLayout()
            Spacer(modifier = Modifier.height(32.dp))
            SetPeopleCountLayout(userData = userData)
        }
    }
}

@Composable
fun SetTalkTimeLayout(modifier: Modifier = Modifier) {
    val times = remember { (5..120 step 5).toList() }
    val pagerState = rememberPagerState(pageCount = { times.size })
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(
                        color = colorResource(R.color.orange_80),
                        shape = MaterialTheme.shapes.medium
                    )
            )
            HorizontalPager(
                modifier = Modifier,
                state = pagerState,
                contentPadding = PaddingValues(horizontal = 136.dp),
                pageSpacing = 16.dp,
            ) { index ->
                TalkTimeItem(
                    modifier = Modifier
//                        .size(100.dp)
                        .align(Alignment.Center),
                    time = times[index],
                    isSelected = pagerState.currentPage == index
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text(
            text = "대화 시간",
            style = MaterialTheme.typography.h4.copy(
                color = MaterialTheme.colors.onSecondary,
                fontSize = 24.sp
            )
        )
    }
}

@Composable
fun TalkTimeItem(
    modifier: Modifier = Modifier,
    time: Int,
    isSelected: Boolean
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$time",
            style = MaterialTheme.typography.h2.copy(
                color = MaterialTheme.colors.onSecondary.copy(
                    alpha = if (isSelected) {
                        1f
                    } else {
                        0.6f
                    }
                ),
                fontWeight = if (isSelected) {
                    FontWeight.Bold
                } else {
                    FontWeight.Medium
                }
            )
        )
    }
}

@Composable
fun SetPeopleCountLayout(
    modifier: Modifier = Modifier,
    userData: UserData?
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.minus(36).dp
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            modifier = Modifier.padding(4.dp),
            text = "인원 수",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        Spacer(modifier = Modifier.height(10.dp))
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2)
        ) {
            item {
                if (userData != null) {
                    TalkUserInfo(
                        modifier = Modifier
                            .width(maxWidth.div(2))
                            .padding(4.dp),
                        userData = userData,
                        isCurrentUser = true
                    )
                }
            }
            item {
                DecreaseTalkUserInfo(
                    modifier = Modifier
                        .width(maxWidth.div(2))
                        .padding(4.dp)
                )
            }
            item {
                IncreaseTalkUserInfo(
                    modifier = Modifier
                        .width(maxWidth.div(2))
                        .padding(4.dp)
                )
            }
        }
    }
}

@Composable
fun IncreaseTalkUserInfo(modifier: Modifier = Modifier) {
    EmptyTalkUserInfo(
        modifier = modifier,
        alignment = Alignment.Center
    ) {
        Icon(
            modifier = it.size(28.dp),
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "increase",
            tint = MaterialTheme.colors.secondary
        )
    }
}

@Composable
fun DecreaseTalkUserInfo(modifier: Modifier = Modifier) {
    EmptyTalkUserInfo(
        modifier = modifier,
        alignment = Alignment.TopEnd
    ) {
        Icon(
            modifier = it
                .padding(6.dp)
                .size(24.dp),
            painter = painterResource(id = R.drawable.ic_close),
            contentDescription = "decrease",
            tint = MaterialTheme.colors.onPrimary
        )
    }
}

@Composable
fun TalkOptionsLayout(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "옵션",
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        Spacer(modifier = Modifier.height(14.dp))
        TalkOptionItem(
            optionName = "타이머",
            optionIcon = painterResource(id = R.drawable.ic_timer)
        )
        Spacer(modifier = Modifier.height(10.dp))
        TalkOptionItem(
            optionName = "녹음 허용",
            optionIcon = painterResource(id = R.drawable.ic_mic)
        )
    }
}

@Composable
fun TalkOptionItem(
    modifier: Modifier = Modifier,
    optionName: String,
    optionIcon: Painter
) {
    Box(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .fillMaxWidth()
            .padding(14.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = optionName,
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
        Icon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(24.dp),
            painter = optionIcon,
            contentDescription = optionName
        )
    }
}