package com.sghore.needtalk.presentation.ui.groups_detail_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.sghore.needtalk.R
import com.sghore.needtalk.domain.model.TalkTopicGroup
import com.sghore.needtalk.presentation.ui.ConfirmWithCancelDialog
import com.sghore.needtalk.presentation.ui.SimpleInputDialog

@Composable
fun GroupsDetailScreen(
    uiState: GroupsDetailUiState,
    onEvent: (GroupsDetailUiEvent) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
                    .clickable { onEvent(GroupsDetailUiEvent.ClickNavigateUp) },
                painter = painterResource(id = R.drawable.ic_back_arrow),
                contentDescription = "navigateUp"
            )
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = "내 대화주제 모음집",
                style = MaterialTheme.typography.h5.copy(color = MaterialTheme.colors.onPrimary)
            )
        }
        LazyVerticalGrid(
            modifier = Modifier.fillMaxSize(),
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(9.dp)
        ) {
            items(uiState.groups.size) { index ->
                GroupItem(
                    modifier = Modifier.padding(5.dp),
                    group = uiState.groups[index],
                    onSelectEdit = { onEvent(GroupsDetailUiEvent.SelectEdit(it)) },
                    onSelectDelete = { onEvent(GroupsDetailUiEvent.SelectRemove(it)) }
                )
            }
        }
    }
}

@Composable
fun GroupItem(
    modifier: Modifier = Modifier,
    group: TalkTopicGroup,
    onSelectEdit: (TalkTopicGroup) -> Unit,
    onSelectDelete: (TalkTopicGroup) -> Unit
) {
    var isExpended by remember { mutableStateOf(false) }
    val maxWidth = LocalConfiguration.current.screenWidthDp.minus(40).dp

    Box(
        modifier = modifier
            .width(maxWidth.div(2))
            .shadow(2.dp, MaterialTheme.shapes.medium)
            .background(color = MaterialTheme.colors.background)
            .padding(8.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier.size(80.dp),
                painter = when (group.id ?: 0) {
                    0 -> painterResource(id = R.drawable.added_group)
                    1 -> painterResource(id = R.drawable.favorite_group)
                    else -> painterResource(id = R.drawable.default_group)
                },
                contentDescription = "groupImage"
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = group.name,
                style = MaterialTheme.typography.body1.copy(
                    color = MaterialTheme.colors.onPrimary
                ),
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }

        if ((group.id ?: 0) > 1) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .clickable { isExpended = true },
                painter = painterResource(id = R.drawable.ic_more),
                contentDescription = "more",
                tint = MaterialTheme.colors.onPrimary
            )
            DropdownMenu(
                offset = DpOffset(maxWidth.div(6), (-80).dp),
                expanded = isExpended,
                onDismissRequest = { isExpended = false }
            ) {
                DropdownMenuItem(
                    onClick = {
                        onSelectEdit(group)
                        isExpended = false
                    }
                ) {
                    Text(text = "수정")
                }
                DropdownMenuItem(onClick = {
                    onSelectDelete(group)
                    isExpended = false
                }) {
                    Text(text = "삭제")
                }
            }
        }
    }
}

@Composable
fun EditGroupDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    group: TalkTopicGroup,
    onEditGroupClick: (TalkTopicGroup) -> Unit
) {
    SimpleInputDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        title = "모음집 수정",
        hint = "모음집 이름",
        startInputData = group.name,
        buttonText = "수정하기",
        onButtonClick = {
            val updateGroup = group.copy(name = it)
            onEditGroupClick(updateGroup)
        }
    )
}

@Composable
fun RemoveGroupDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    group: TalkTopicGroup,
    onRemoveGroupClick: (TalkTopicGroup) -> Unit
) {
    ConfirmWithCancelDialog(
        modifier = modifier,
        onDismiss = onDismiss,
        title = "모음집 삭제",
        message = "\"${group.name}\"\n해당 모음집을 삭제하시겠습니까?",
        confirmText = "삭제하기",
        cancelText = "취소",
        onConfirm = { onRemoveGroupClick(group) }
    )
}