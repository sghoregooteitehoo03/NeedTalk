package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.compose.collectAsLazyPagingItems
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.TalkTopicCategoryTag

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TalkTopicsScreen(
    uiState: TalkTopicsDetailUiState,
    onEvent: (TalkTopicsDetailUiEvent) -> Unit
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
                    .clip(CircleShape)
                    .align(Alignment.CenterStart)
                    .clickable { onEvent(TalkTopicsDetailUiEvent.ClickNavigateUp) },
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.End
                ) {
                    ListFilter(
                        orderType = uiState.orderType,
                        onSelectOrderType = {
                            onEvent(TalkTopicsDetailUiEvent.SelectOrderType(it))
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            talkTopics?.let {
                items(it.itemCount) { index ->
                    TalkTopicItem(
                        talkTopic = it[index]!!,
                        onFavoriteClick = { topicId, isFavorite ->
                            onEvent(
                                TalkTopicsDetailUiEvent.ClickFavorite(
                                    topicId = topicId,
                                    isFavorite = isFavorite
                                )
                            )
                        },
                        onSaveClick = { topicId ->
                            onEvent(TalkTopicsDetailUiEvent.ClickBookmark(topicId))
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@ExperimentalMaterialApi
@Composable
fun ListFilter(
    modifier: Modifier = Modifier,
    orderType: OrderType,
    onSelectOrderType: (OrderType) -> Unit
) {
    var isExpended by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.width(128.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(modifier = modifier.clickable { isExpended = true }) {
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
        DropdownMenu(
            offset = DpOffset(16.dp, (-24).dp),
            expanded = isExpended,
            onDismissRequest = { isExpended = false }
        ) {
            DropdownMenuItem(onClick = {
                if (orderType !is OrderType.Popular) {
                    onSelectOrderType(OrderType.Popular)
                }
                isExpended = false
            }) {
                Text(text = "인기도 순")
            }
            DropdownMenuItem(onClick = {
                if (orderType !is OrderType.Recently) {
                    onSelectOrderType(OrderType.Recently)
                }
                isExpended = false
            }) {
                Text(text = "최근 제작 순")
            }
        }
    }
}

@Composable
fun TalkTopicItem(
    modifier: Modifier = Modifier,
    talkTopic: TalkTopic,
    onFavoriteClick: (String, Boolean) -> Unit,
    onSaveClick: (String) -> Unit
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
                icon = if (talkTopic.isFavorite) {
                    painterResource(id = R.drawable.ic_heart)
                } else {
                    painterResource(id = R.drawable.ic_heart_border)
                },
                text = talkTopic.favoriteCount.toString(),
                color = if (talkTopic.isFavorite) {
                    MaterialTheme.colors.secondary
                } else {
                    MaterialTheme.colors.onPrimary
                },
                onClick = {
                    onFavoriteClick(talkTopic.topicId, !talkTopic.isFavorite)
                }
            )
            Spacer(modifier = Modifier.width(24.dp))
            TalkTopicItemButton(
                icon = painterResource(id = R.drawable.ic_bookmark),
                text = "저장",
                color = MaterialTheme.colors.onPrimary,
                onClick = { onSaveClick(talkTopic.topicId) }
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
        modifier = modifier.clickable { onClick() },
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

@Composable
fun SaveTopicDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    myGroups: List<TalkTopicGroup>
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = modifier
                .fillMaxHeight(0.65f)
                .padding(14.dp)
        ) {
            ConstraintLayout(modifier = Modifier.fillMaxSize()) {
                val (title, content, button) = createRefs()
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(title) {
                        top.linkTo(parent.top)
                    }
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        text = "대화 모음집 저장",
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
                LazyColumn(modifier = Modifier
                    .constrainAs(content) {
                        top.linkTo(title.bottom, 16.dp)
                        bottom.linkTo(button.top, 16.dp)
                        height = Dimension.fillToConstraints
                    }
                ) {
                    items(myGroups.size) { index ->
                        SelectGroupItem(group = myGroups[index])
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        AddGroupItem()
                    }
                }
                DefaultButton(
                    modifier = Modifier.constrainAs(button) {
                        bottom.linkTo(parent.bottom)
                    },
                    buttonHeight = 46.dp,
                    text = "저장하기",
                    textStyle = MaterialTheme.typography.body1.copy(
                        color = MaterialTheme.colors.onSecondary
                    ),
                    onClick = {}
                )
            }
        }
    }
}

@Composable
fun SelectGroupItem(
    modifier: Modifier = Modifier,
    group: TalkTopicGroup
) {
    val isEnabled = remember(group.id) { (group.id ?: 0) > 2 }
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

    ConstraintLayout(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .clip(shape = MaterialTheme.shapes.medium)
            .then(
                if (isEnabled) {
                    Modifier
                } else {
                    Modifier.alpha(0.5f)
                }
            )
            .padding(14.dp)
    ) {
        val (icon, title, checkBox) = createRefs()
        Image(
            modifier = Modifier
                .size(48.dp)
                .constrainAs(icon) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
            painter = iconPainter,
            contentDescription = "GroupIcon"
        )
        Text(
            modifier = Modifier.constrainAs(title) {
                start.linkTo(icon.end, 10.dp)
                end.linkTo(checkBox.start, 10.dp)
                bottom.linkTo(icon.bottom)
                top.linkTo(icon.top)
            },
            text = group.name,
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
        )
    }
}

@Composable
fun AddGroupItem(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .background(
                color = MaterialTheme.colors.background,
                shape = MaterialTheme.shapes.medium
            )
            .clip(shape = MaterialTheme.shapes.medium)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier
                .size(48.dp),
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = "AddGroup"
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = "새 모음집 제작",
            style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
        )
    }
}