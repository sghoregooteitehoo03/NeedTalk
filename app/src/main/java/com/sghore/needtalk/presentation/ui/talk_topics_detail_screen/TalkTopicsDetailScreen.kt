package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.DefaultButton
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.SimpleInputDialog
import com.sghore.needtalk.presentation.ui.TalkTopicCategoryTag
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TalkTopicsScreen(
    userData: UserData?,
    uiState: TalkTopicsDetailUiState,
    onEvent: (TalkTopicsDetailUiEvent) -> Unit
) {
    val talkTopics = uiState.talkTopics?.collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val isLoading by remember(talkTopics) { derivedStateOf { talkTopics?.loadState?.refresh is LoadState.Loading } }
    if (uiState.talkTopicsDetailType is TalkTopicsDetailType.PopularType) {
        if (uiState.talkTopicsDetailType.index != 0) {
            var isScrolled by remember { mutableStateOf(false) }

            if (!isLoading && !isScrolled) {
                LaunchedEffect(key1 = true) { // 선택한 리스트로 바로 이동하게 구현
                    listState.animateScrollToItem(index = uiState.talkTopicsDetailType.index)
                    isScrolled = true
                }
            }
        }
    }

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
                text = uiState.talkTopicsDetailType?.title ?: "",
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MaterialTheme.colors.onPrimary)
            }
        } else {
            talkTopics?.let {
                if (it.itemCount == 0) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "대화주제가 없습니다.",
                            style = MaterialTheme.typography.h4.copy(
                                color = MaterialTheme.colors.onPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "다양한 대화주제들을 추가해보세요!",
                            style = MaterialTheme.typography.h5.copy(
                                color = colorResource(id = R.color.gray)
                            )
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        state = listState
                    ) {
                        if (uiState.talkTopicsDetailType is TalkTopicsDetailType.CategoryType) {
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
                        }

                        items(it.itemCount) { index ->
                            val talkTopic = it[index]!!
                            if (talkTopic.isUpload || !talkTopic.isPublic) {
                                val favoriteHistory = uiState.favoriteHistory[talkTopic.topicId]
                                val favoriteCounts =
                                    if (favoriteHistory == null) {
                                        FavoriteCounts(
                                            talkTopic.isFavorite,
                                            talkTopic.favoriteCount
                                        )
                                    } else {
                                        FavoriteCounts(
                                            favoriteHistory.isFavorite,
                                            favoriteHistory.count
                                        )
                                    }

                                TalkTopicItem(
                                    talkTopic = talkTopic,
                                    userId = userData?.userId ?: "",
                                    favoriteCounts = favoriteCounts,
                                    onFavoriteClick = { topicId, isFavorite ->
                                        onEvent(
                                            TalkTopicsDetailUiEvent.ClickFavorite(
                                                topicId = topicId,
                                                isFavorite = isFavorite
                                            )
                                        )
                                    },
                                    onSaveClick = { _talkTopic ->
                                        onEvent(
                                            TalkTopicsDetailUiEvent.ClickBookmark(
                                                _talkTopic
                                            )
                                        )
                                    },
                                    onRemoveClick = { _talkTopic ->
                                        onEvent(
                                            TalkTopicsDetailUiEvent.ClickRemove(
                                                _talkTopic
                                            )
                                        )
                                    }
                                )
                            } else {
                                TalkTopicItem(
                                    modifier = Modifier.alpha(0.6f),
                                    talkTopic = talkTopic,
                                    userId = userData?.userId ?: "",
                                    favoriteCounts = FavoriteCounts(false, 0),
                                    onFavoriteClick = { _, _ -> },
                                    onSaveClick = { }
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
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
    userId: String,
    favoriteCounts: FavoriteCounts,
    onFavoriteClick: (String, Boolean) -> Unit,
    onSaveClick: (TalkTopic) -> Unit,
    onRemoveClick: (TalkTopic) -> Unit = {}
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
            if (talkTopic.isUpload || !talkTopic.isPublic) {
                TalkTopicItemButton(
                    icon = if (favoriteCounts.isFavorite) {
                        painterResource(id = R.drawable.ic_heart)
                    } else {
                        painterResource(id = R.drawable.ic_heart_border)
                    },
                    text = favoriteCounts.count.toString(),
                    color = if (favoriteCounts.isFavorite) {
                        MaterialTheme.colors.secondary
                    } else {
                        MaterialTheme.colors.onPrimary.copy(
                            alpha = if (talkTopic.isPublic) {
                                1f
                            } else {
                                0.6f
                            }
                        )
                    },
                    onClick = {
                        if (userId != talkTopic.uid) {
                            onFavoriteClick(talkTopic.topicId, !favoriteCounts.isFavorite)
                        }
                    }
                )
                Spacer(modifier = Modifier.width(24.dp))
                TalkTopicItemButton(
                    icon = painterResource(id = R.drawable.ic_bookmark),
                    text = "저장",
                    color = MaterialTheme.colors.onPrimary,
                    onClick = { onSaveClick(talkTopic) }
                )
                if (talkTopic.uid == userId) {
                    Spacer(modifier = Modifier.width(24.dp))
                    TalkTopicItemButton(
                        icon = painterResource(id = R.drawable.ic_trash),
                        text = "삭제",
                        color = MaterialTheme.colors.onPrimary,
                        onClick = { onRemoveClick(talkTopic) }
                    )
                }
            } else {
                TalkTopicItemButton(
                    icon = painterResource(id = R.drawable.ic_review),
                    text = "검토 중",
                    color = MaterialTheme.colors.onPrimary,
                    onClick = { }
                )
            }
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
    myGroupsFlow: Flow<List<TalkTopicGroup>>,
    onAddGroupClick: (String) -> Unit,
    onSaveClick: (Map<Int, Boolean>) -> Unit
) {
    val myGroups by myGroupsFlow.collectAsStateWithLifecycle(
        initialValue = listOf(),
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val selectedGroupMap = remember { mutableStateMapOf<Int, Boolean>() }
    var addGroupDialog by remember { mutableStateOf<DialogScreen>(DialogScreen.DialogDismiss) }

    LaunchedEffect(key1 = myGroups) {
        myGroups.filter { it.isIncludeTopic }.forEach {
            selectedGroupMap[it.id ?: 0] = true
        }
    }

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
                        val myGroup = myGroups[index]
                        SelectGroupItem(
                            group = myGroup,
                            isSelected = selectedGroupMap.getOrDefault(myGroup.id ?: 0, false),
                            onClick = { isSelected ->
                                selectedGroupMap[myGroup.id ?: 0] = isSelected
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    item {
                        AddGroupItem(onClick = {
                            addGroupDialog = DialogScreen.DialogAddOrEditGroup()
                        })
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
                    onClick = {
                        onSaveClick(selectedGroupMap)
                        onDismiss()
                    }
                )
            }
        }
    }

    if (addGroupDialog is DialogScreen.DialogAddOrEditGroup) {
        AddGroupDialog(
            modifier = Modifier
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    color = MaterialTheme.colors.background,
                    shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                )
                .padding(14.dp),
            onDismiss = { addGroupDialog = DialogScreen.DialogDismiss },
            onAddGroupClick = onAddGroupClick
        )
    }
}

@Composable
fun AddGroupDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAddGroupClick: (String) -> Unit
) {
    SimpleInputDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        title = "모음집 제작",
        hint = "모음집 이름",
        startInputData = "",
        buttonText = "제작하기",
        onButtonClick = onAddGroupClick
    )
}

@Composable
fun SelectGroupItem(
    modifier: Modifier = Modifier,
    group: TalkTopicGroup,
    isSelected: Boolean,
    onClick: (Boolean) -> Unit
) {
    val isEnabled = remember(group.id) { (group.id ?: 0) > 1 }
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
                color = if (isSelected) {
                    colorResource(id = R.color.light_orange)
                } else {
                    MaterialTheme.colors.background
                },
                shape = MaterialTheme.shapes.medium
            )
            .clip(shape = MaterialTheme.shapes.medium)
            .then(
                if (isEnabled) {
                    Modifier.clickable { onClick(!isSelected) }
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
                width = Dimension.fillToConstraints
            },
            maxLines = 2,
            text = group.name,
            style = MaterialTheme.typography.h5.copy(
                color = MaterialTheme.colors.onPrimary,
                textAlign = TextAlign.Start
            )
        )
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .constrainAs(checkBox) {
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    }
                    .clip(MaterialTheme.shapes.small)
                    .background(
                        color = MaterialTheme.colors.secondary,
                        shape = MaterialTheme.shapes.small
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    modifier = Modifier.size(18.dp),
                    painter = painterResource(id = R.drawable.ic_check),
                    contentDescription = "checkBox",
                    tint = MaterialTheme.colors.onSecondary
                )
            }
        }
    }
}

@Composable
fun AddGroupItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit
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
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(48.dp),
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