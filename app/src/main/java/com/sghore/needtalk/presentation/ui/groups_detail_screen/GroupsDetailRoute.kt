package com.sghore.needtalk.presentation.ui.groups_detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.UiScreen
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsDetailType
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json

@Composable
fun GroupsDetailRoute(
    viewModel: GroupsDetailViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    navigateToTalkTopicsDetailScreen: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is GroupsDetailUiEvent.ClickNavigateUp -> navigateUp()
                is GroupsDetailUiEvent.SelectEdit -> viewModel.setDialogScreen(
                    DialogScreen.DialogAddOrEditGroup(event.group)
                )

                is GroupsDetailUiEvent.SelectRemove -> viewModel.setDialogScreen(
                    DialogScreen.DialogRemoveGroup(event.group)
                )

                is GroupsDetailUiEvent.ClickGroupItem -> {
                    val detailTypeJson =
                        Json.encodeToString(
                            TalkTopicsDetailType.serializer(),
                            TalkTopicsDetailType.GroupType(
                                code = event.group.id ?: 0,
                                _title = event.group.name
                            )
                        )
                    val route = UiScreen.TalkTopicsDetailScreen.route + "?type=${detailTypeJson}"

                    navigateToTalkTopicsDetailScreen(route)
                }
            }
        }
    }

    GroupsDetailScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvent
    )

    when (val dialogScreen = uiState.dialogScreen) {
        is DialogScreen.DialogAddOrEditGroup -> {
            EditGroupDialog(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(14.dp),
                onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                group = dialogScreen.group!!,
                onEditGroupClick = viewModel::editTalkTopicGroup
            )
        }

        is DialogScreen.DialogRemoveGroup -> {
            RemoveGroupDialog(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(14.dp),
                onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                group = dialogScreen.group,
                onRemoveGroupClick = viewModel::removeTalkTopicGroup
            )
        }

        else -> {}
    }
}