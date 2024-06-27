package com.sghore.needtalk.presentation.ui.timer_screen.pinned_talktopic_dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.PinnedTalkTopic
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.presentation.ui.TalkTopicCategoryTag
import com.sghore.needtalk.presentation.ui.talk_topics_detail_screen.TalkTopicItemButton
import kotlinx.coroutines.flow.Flow

@Composable
fun PinnedTalkTopicDialog(
    modifier: Modifier = Modifier,
    viewModel: PinnedTalkTopicViewModel = hiltViewModel(),
    userId: String,
    onDismiss: () -> Unit,
    onPinnedTalkTopic: (TalkTopic) -> Unit
) {
    BottomSheetDialog(onDismissRequest = {
        onDismiss()
        viewModel.clearData()
    }) {
        Column(modifier = modifier.fillMaxHeight(0.65f)) {
            when (viewModel.page) {
                1 -> {
                    SelectGroup(
                        onDismiss = onDismiss,
                        talkTopicGroups = viewModel.talkTopicGroups,
                        onClickGroup = {
                            viewModel.selectGroup(
                                group = it,
                                userId = userId
                            )
                        }
                    )
                }

                2 -> {
                    PinTalkTopic(
                        navigateUp = { viewModel.clearData() },
                        onDismiss = {
                            onDismiss()
                            viewModel.clearData()
                        },
                        title = viewModel.title,
                        talkTopicsFlow = viewModel.talkTopics!!,
                        onPinnedTalkTopic = {
                            onDismiss()
                            viewModel.clearData()
                            onPinnedTalkTopic(it)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectGroup(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    talkTopicGroups: List<TalkTopicGroup>,
    onClickGroup: (TalkTopicGroup) -> Unit,
) {
    Column(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "대화주제 지정",
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterEnd)
                    .clickable { onDismiss() },
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Close"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(talkTopicGroups.size) { index ->
                GroupItem(
                    group = talkTopicGroups[index],
                    onClick = onClickGroup
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun PinTalkTopic(
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
    onDismiss: () -> Unit,
    title: String,
    talkTopicsFlow: Flow<PagingData<TalkTopic>>,
    onPinnedTalkTopic: (TalkTopic) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        val talkTopics = talkTopicsFlow.collectAsLazyPagingItems()
        Box(modifier = Modifier.fillMaxWidth()) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterStart)
                    .clickable { navigateUp() },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "NavigateUp"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = title,
                style = MaterialTheme.typography.h5.copy(
                    color = MaterialTheme.colors.onPrimary,
                    fontWeight = FontWeight.Bold
                )
            )
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterEnd)
                    .clickable { onDismiss() },
                painter = painterResource(id = R.drawable.ic_close),
                contentDescription = "Close"
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(talkTopics.itemCount) { index ->
                TalkTopicItem(
                    talkTopic = talkTopics[index]!!,
                    onPinnedTalkTopic = onPinnedTalkTopic
                )
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
fun GroupItem(
    modifier: Modifier = Modifier,
    group: TalkTopicGroup,
    onClick: (TalkTopicGroup) -> Unit
) {
    val iconPainter = when (group.id) {
        0 -> {
            painterResource(id = R.drawable.added_group)
        }

        1 -> {
            painterResource(id = R.drawable.favorite_group)
        }

        else -> {
            painterResource(id = R.drawable.default_group)
        }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .clip(shape = MaterialTheme.shapes.medium)
            .clickable { onClick(group) }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            modifier = Modifier.size(48.dp),
            painter = iconPainter,
            contentDescription = "GroupIcon"
        )
        Text(
            maxLines = 2,
            text = group.name,
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Start
            )
        )
    }
}

@Composable
fun TalkTopicItem(
    modifier: Modifier = Modifier,
    talkTopic: TalkTopic,
    onPinnedTalkTopic: (TalkTopic) -> Unit
) {
    val maxWidth = LocalConfiguration.current.screenWidthDp.dp.minus((28 + 72).dp)
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(maxWidth)
            .shadow(2.dp, MaterialTheme.shapes.large)
            .clip(MaterialTheme.shapes.large)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.large
            )
            .padding(14.dp),
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
                        textStyle = MaterialTheme.typography.subtitle1.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                    if (index < 1) {
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = talkTopic.topic,
                style = MaterialTheme.typography.h4.copy(
                    color = MaterialTheme.colors.onPrimary,
                    textAlign = TextAlign.Center
                )
            )
            Spacer(modifier = Modifier.height(12.dp))
        }
        Icon(
            modifier = Modifier
                .size(24.dp)
                .align(Alignment.BottomCenter)
                .clip(CircleShape)
                .clickable { onPinnedTalkTopic(talkTopic) },
            painter = painterResource(id = R.drawable.ic_pin),
            contentDescription = "Pin",
            tint = colorResource(id = R.color.gray)
        )
    }
}