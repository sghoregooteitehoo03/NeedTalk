package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.ConfirmWithCancelDialog
import com.sghore.needtalk.presentation.ui.DialogScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TalkTopicsDetailRoute(
    userData: UserData?,
    viewModel: TalkTopicsDetailViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is TalkTopicsDetailUiEvent.ClickNavigateUp -> navigateUp()
                is TalkTopicsDetailUiEvent.SelectOrderType ->
                    viewModel.selectOrderType(event.orderType)

                is TalkTopicsDetailUiEvent.ClickBookmark ->
                    viewModel.setOpenDialog(DialogScreen.DialogSaveTopic(event.talkTopic))

                is TalkTopicsDetailUiEvent.ClickFavorite -> viewModel.setFavorite(
                    topicId = event.topicId,
                    uid = userData?.userId ?: "",
                    isFavorite = event.isFavorite
                )

                is TalkTopicsDetailUiEvent.ClickRemove -> viewModel.setOpenDialog(
                    DialogScreen.DialogRemoveTalkTopic(
                        event.talkTopic
                    )
                )
            }
        }
    }

    TalkTopicsScreen(
        userData = userData,
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )

    when (val dialogScreen = uiState.dialogScreen) {
        is DialogScreen.DialogSaveTopic -> {
            SaveTopicDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    ),
                onDismiss = { viewModel.setOpenDialog(DialogScreen.DialogDismiss) },
                myGroupsFlow = viewModel.getAllTalkTopicGroups(dialogScreen.talkTopic.topicId),
                onAddGroupClick = viewModel::addGroup,
                onSaveClick = {
                    viewModel.saveTalkTopicGroup(
                        selectedGroup = it,
                        topicId = dialogScreen.talkTopic.topicId,
                        isPublic = dialogScreen.talkTopic.isPublic
                    )
                }
            )
        }

        is DialogScreen.DialogRemoveTalkTopic -> {
            ConfirmWithCancelDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(14.dp)
                    .fillMaxHeight(0.2f),
                onDismiss = { viewModel.setOpenDialog(DialogScreen.DialogDismiss) },
                title = "대화주제 삭제",
                message = "제작하신 대화주제를 삭제하시겠습니까?",
                confirmText = "삭제하기",
                cancelText = "취소",
                onConfirm = { viewModel.removeTalkTopic(talkTopic = dialogScreen.talkTopic) }
            )
        }

        else -> {}
    }
}