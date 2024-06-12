package com.sghore.needtalk.presentation.ui.talk_topics_detail_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.UserData
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

                is TalkTopicsDetailUiEvent.ClickBookmark -> TODO()
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
}