package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.presentation.ui.TalkTopicCategoryTag

@Composable
fun TalkTopicsScreen(
    uiState: TalkTopicsDetailUiState,
) {
    val talkTopics = uiState.talkTopics?.collectAsLazyPagingItems()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Box(
            modifier = Modifier
                .padding(start = 14.dp, end = 14.dp)
                .fillMaxWidth()
                .height(56.dp),
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart),
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "navigateUp"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "타이틀",
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            item {
                ListFilter(orderType = uiState.orderType)
                Spacer(modifier = Modifier.height(10.dp))
            }

            talkTopics?.let {
                items(it.itemCount) { index ->
                    TalkTopicItem(talkTopic = it[index]!!)
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun ListFilter(
    modifier: Modifier = Modifier,
    orderType: OrderType
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = when (orderType) {
                    is OrderType.Popular -> {
                        "인기도 순"
                    }

                    is OrderType.Recently -> {
                        "최근 제작 순"
                    }
                },
                style = MaterialTheme.typography.body1.copy(color = colorResource(id = R.color.gray))
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.ic_filter),
                contentDescription = "filter",
                tint = colorResource(id = R.color.gray)
            )
        }
    }
}

@Composable
fun TalkTopicItem(
    modifier: Modifier = Modifier,
    talkTopic: TalkTopic
) {
    Box(
        modifier = modifier
            .height(460.dp)
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.large
            )
            .padding(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                val tagList = remember {
                    listOf(
                        talkTopic.category1.title,
                        talkTopic.category2?.title ?: "",
                        talkTopic.category3?.title ?: ""
                    ).filter { it.isNotEmpty() }
                }

                tagList.forEachIndexed { index, tagName ->
                    TalkTopicCategoryTag(
                        tagName = tagName,
                        paddingValues = PaddingValues(
                            top = 6.dp,
                            bottom = 6.dp,
                            start = 12.dp,
                            end = 12.dp
                        ),
                        textStyle = MaterialTheme.typography.body1.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                    if (index < 1) {
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = talkTopic.topic,
                style = MaterialTheme.typography.h3.copy(
                    color = MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Row(modifier = Modifier.align(Alignment.BottomCenter)) {
            TalkTopicItemButton(
                icon = painterResource(id = R.drawable.ic_heart_border),
                text = talkTopic.favoriteCount.toString(),
                color = MaterialTheme.colors.onPrimary,
                onClick = {}
            )
            Spacer(modifier = Modifier.width(24.dp))
            TalkTopicItemButton(
                icon = painterResource(id = R.drawable.ic_bookmark),
                text = "저장",
                color = MaterialTheme.colors.onPrimary,
                onClick = {}
            )
        }
    }
}

@Composable
fun TalkTopicItemButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    text: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(28.dp),
            painter = icon,
            contentDescription = "Icon",
            tint = color
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.h5.copy(
                color = color
            )
        )
    }
}