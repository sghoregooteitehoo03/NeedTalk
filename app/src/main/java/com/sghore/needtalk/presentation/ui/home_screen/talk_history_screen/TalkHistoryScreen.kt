package com.sghore.needtalk.presentation.ui.home_screen.talk_history_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.getFileSizeToStr
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun TalkHistoryScreen(
    uiState: TalkHistoryUiState,
    onClickTalkHistory: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        val talkHistories = uiState.talkHistory?.collectAsLazyPagingItems()
        talkHistories?.let {
            val isLoading by remember(it) {
                derivedStateOf { it.loadState.refresh is LoadState.Loading }
            }
            if (!isLoading) {
                if (it.itemCount == 0) {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_empty_history),
                            contentDescription = "EmptyHistory",
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "대화기록이 존재하지 않습니다.",
                            style = MaterialTheme.typography.h5.copy(
                                color = colorResource(id = R.color.gray),
                                fontSize = 18.sp
                            )
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.padding(top = 10.dp)) {
                        items(it.itemCount) { index ->
                            TalkHistoryItem(
                                modifier = Modifier.padding(
                                    top = 4.dp,
                                    start = 14.dp,
                                    end = 14.dp,
                                    bottom = 4.dp
                                ),
                                talkHistory = it[index]!!,
                                onClick = { talkHistory -> onClickTalkHistory(talkHistory.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TalkHistoryItem(
    modifier: Modifier = Modifier,
    talkHistory: TalkHistory,
    onClick: (TalkHistory) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .shadow(2.dp, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.large
            )
            .clickable { onClick(talkHistory) }
            .padding(14.dp)
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
            val (recordFile, info, timestamp, clips) = createRefs()
            if (talkHistory.recordFile != null) {
                Box(
                    modifier = Modifier
                        .constrainAs(recordFile) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                        .size(48.dp)
                        .background(
                            color = colorResource(id = R.color.light_gray),
                            shape = MaterialTheme.shapes.medium
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        modifier = Modifier.size(40.dp),
                        painter = painterResource(id = R.drawable.ic_noize),
                        contentDescription = "Record",
                        tint = MaterialTheme.colors.secondary
                    )
                }
            }
            Column(
                modifier = Modifier
                    .constrainAs(info) {
                        top.linkTo(recordFile.top)
                        bottom.linkTo(recordFile.bottom)
                        if (talkHistory.recordFile != null) {
                            start.linkTo(recordFile.end, 12.dp)
                        } else {
                            start.linkTo(parent.start)
                        }
                        end.linkTo(timestamp.start, 12.dp)
                        width = Dimension.fillToConstraints
                    }
                    .height(48.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = talkHistory.talkTitle,
                    style = MaterialTheme.typography.h5.copy(
                        color = MaterialTheme.colors.onPrimary
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row {
                    Text(
                        text = SimpleDateFormat(
                            "HH:mm:ss",
                            Locale.KOREA
                        ).format(talkHistory.talkTime.minus(32400000)),
                        style = MaterialTheme.typography.body1.copy(
                            color = colorResource(id = R.color.gray)
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = getFileSizeToStr(talkHistory.recordFile?.length() ?: 0),
                        style = MaterialTheme.typography.body1.copy(
                            color = colorResource(id = R.color.gray)
                        )
                    )
                }
            }
            Text(
                modifier = Modifier.constrainAs(timestamp) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
                text = SimpleDateFormat(
                    "yy.MM.dd (E)",
                    Locale.KOREA
                ).format(talkHistory.createTimeStamp),
                style = MaterialTheme.typography.subtitle1.copy(
                    color = colorResource(id = R.color.gray)
                )
            )
            Row(
                modifier = Modifier.constrainAs(clips) {
                    top.linkTo(timestamp.bottom, 6.dp)
                    end.linkTo(parent.end)
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                val color = if (talkHistory.clipCount != 0) {
                    MaterialTheme.colors.secondary
                } else {
                    colorResource(id = R.color.gray)
                }

                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.ic_star),
                    contentDescription = "clip",
                    tint = color
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = talkHistory.clipCount.toString(),
                    style = MaterialTheme.typography.body1.copy(
                        color = color
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        LazyRow {
            items(talkHistory.users) { user ->
                OtherUser(userData = user)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun OtherUser(
    modifier: Modifier = Modifier,
    userData: UserData
) {
    Row(
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .background(
                color = colorResource(id = R.color.light_gray),
                shape = MaterialTheme.shapes.medium
            )
            .padding(start = 4.dp, end = 4.dp, top = 6.dp, bottom = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(20.dp),
            bitmap = userData.profileImage,
            contentDescription = "UserImage"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = userData.name,
            style = MaterialTheme.typography.subtitle1.copy(
                color = MaterialTheme.colors.onPrimary
            )
        )
    }
}