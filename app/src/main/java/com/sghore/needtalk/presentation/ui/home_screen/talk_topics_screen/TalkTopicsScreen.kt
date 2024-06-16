package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.TalkTopicCategory
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.TalkTopicCategoryTag

@Composable
fun TalkTopicsScreen(
    uiState: TalkTopicsUiState,
    onEvent: (TalkTopicsUiEvent) -> Unit
) {
    if (uiState.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
        }
    } else {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            DefaultButton(
                text = "대화주제 제작하기",
                onClick = { onEvent(TalkTopicsUiEvent.ClickAddTopic) }
            )
            Spacer(modifier = Modifier.height(28.dp))
            CategoryLayout(
                onClickCategory = { onEvent(TalkTopicsUiEvent.ClickTopicCategory(it)) }
            )
            Spacer(modifier = Modifier.height(28.dp))
            PopularTalkTopicLayout(
                popularTalkTopics = uiState.popularTalkTopics,
                onMoreClick = { onEvent(TalkTopicsUiEvent.ClickPopularMore) },
                onTalkTopicClick = { onEvent(TalkTopicsUiEvent.ClickTalkTopic(it)) }
            )
            Spacer(modifier = Modifier.height(28.dp))
            TalkTopicGroupLayout(
                talkTopicGroups = uiState.talkTopicGroups,
                onMoreClick = { onEvent(TalkTopicsUiEvent.ClickGroupMore) },
                onGroupClick = { onEvent(TalkTopicsUiEvent.ClickGroup(it)) }
            )
        }
    }
}

@Composable
fun CategoryLayout(
    modifier: Modifier = Modifier,
    onClickCategory: (TalkTopicCategory) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "카테고리",
            style = MaterialTheme.typography.h5.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary,
                fontSize = 18.sp
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryItem(
                talkTopicTopicCategory = TalkTopicCategory.Friend,
                onClick = { onClickCategory(TalkTopicCategory.Friend) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                talkTopicTopicCategory = TalkTopicCategory.Couple,
                onClick = { onClickCategory(TalkTopicCategory.Couple) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                talkTopicTopicCategory = TalkTopicCategory.Family,
                onClick = { onClickCategory(TalkTopicCategory.Family) }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            CategoryItem(
                talkTopicTopicCategory = TalkTopicCategory.Balance,
                onClick = { onClickCategory(TalkTopicCategory.Balance) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                talkTopicTopicCategory = TalkTopicCategory.SmallTalk,
                onClick = { onClickCategory(TalkTopicCategory.SmallTalk) }
            )
            Spacer(modifier = Modifier.width(10.dp))
            CategoryItem(
                talkTopicTopicCategory = TalkTopicCategory.DeepTalk,
                onClick = { onClickCategory(TalkTopicCategory.DeepTalk) }
            )
        }
    }
}

@Composable
fun CategoryItem(
    modifier: Modifier = Modifier,
    talkTopicTopicCategory: TalkTopicCategory,
    onClick: () -> Unit
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.minus(48).dp
    val width = maxWidth.div(3)

    Column(
        modifier = Modifier.clickable { onClick() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = modifier
                .width(width)
                .clip(MaterialTheme.shapes.medium)
        ) {
            Image(
                modifier = Modifier.height(68.dp),
                painter = painterResource(id = talkTopicTopicCategory.imageRes),
                contentDescription = talkTopicTopicCategory.title,
                contentScale = ContentScale.Crop
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = talkTopicTopicCategory.title,
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary)
        )
    }
}

@Composable
fun PopularTalkTopicLayout(
    modifier: Modifier = Modifier,
    popularTalkTopics: List<TalkTopic>,
    onMoreClick: () -> Unit,
    onTalkTopicClick: (index: Int) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "인기 대화주제",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 18.sp
                )
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onMoreClick() },
                text = "더보기",
                style = MaterialTheme.typography.body1.copy(
                    color = colorResource(id = R.color.gray),
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            items(popularTalkTopics.size) { index ->
                TalkTopicItem(
                    talkTopic = popularTalkTopics[index],
                    onClick = { onTalkTopicClick(index) }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }
    }
}

@Composable
fun TalkTopicItem(
    modifier: Modifier = Modifier,
    talkTopic: TalkTopic,
    onClick: () -> Unit
) {
    val tagList = remember {
        listOf(
            talkTopic.category1.title,
            talkTopic.category2?.title ?: "",
            talkTopic.category3?.title ?: ""
        ).filter { it.isNotEmpty() }
    }

    Box(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colors.background, shape = MaterialTheme.shapes.medium)
            .width(200.dp)
            .height(120.dp)
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Text(
            modifier = Modifier.align(Alignment.TopStart),
            text = talkTopic.topic,
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(modifier = Modifier.align(Alignment.BottomStart)) {
            items(tagList.size) {
                TalkTopicCategoryTag(
                    tagName = tagList[it],
                    paddingValues = PaddingValues(
                        top = 6.dp, bottom = 6.dp, start = 12.dp, end = 12.dp
                    ),
                    textStyle = MaterialTheme.typography.subtitle1.copy(
                        color = MaterialTheme.colors.onPrimary
                    )
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}

@Composable
fun TalkTopicGroupLayout(
    modifier: Modifier = Modifier,
    talkTopicGroups: List<TalkTopicGroup>,
    onMoreClick: () -> Unit,
    onGroupClick: (TalkTopicGroup) -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.CenterStart),
                text = "내 대화주제 모음집",
                style = MaterialTheme.typography.h5.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onPrimary,
                    fontSize = 18.sp
                )
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .clickable { onMoreClick() },
                text = "더보기",
                style = MaterialTheme.typography.body1.copy(
                    color = colorResource(id = R.color.gray),
                )
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            items(talkTopicGroups.size) { index ->
                TalkTopicGroupItem(
                    talkTopicGroup = talkTopicGroups[index],
                    onClick = { onGroupClick(it) }
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
fun TalkTopicGroupItem(
    modifier: Modifier = Modifier,
    talkTopicGroup: TalkTopicGroup,
    onClick: (TalkTopicGroup) -> Unit
) {
    Column(
        modifier = modifier
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colors.background, shape = MaterialTheme.shapes.medium)
            .size(120.dp)
            .clickable { onClick(talkTopicGroup) }
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            modifier = Modifier.size(80.dp),
            painter = painterResource(
                id = when (talkTopicGroup.id) {
                    0 -> {
                        R.drawable.added_group
                    }

                    1 -> {
                        R.drawable.favorite_group
                    }

                    else -> {
                        R.drawable.default_group
                    }
                }
            ),
            contentDescription = "FolderImage"
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = talkTopicGroup.name,
            style = MaterialTheme.typography.body1.copy(color = MaterialTheme.colors.onPrimary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}