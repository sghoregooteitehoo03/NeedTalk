package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.compose.foundation.background
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
                    viewModel.setOpenDialog(DialogScreen.DialogSaveTopic)

                is TalkTopicsDetailUiEvent.ClickFavorite -> viewModel.setFavorite(
                    topicId = event.topicId,
                    uid = userData?.userId ?: "",
                    isFavorite = event.isFavorite
                )
            }
        }
    }

    TalkTopicsScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )

    when (uiState.dialogScreen) {
        is DialogScreen.DialogSaveTopic -> {
            SaveTopicDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    ),
                onDismiss = { viewModel.setOpenDialog(DialogScreen.DialogDismiss) },
                myGroupsFlow = viewModel.getAllTalkTopicGroups(),
                onAddGroupClick = viewModel::addGroup
            )
        }

        else -> {}
    }
}