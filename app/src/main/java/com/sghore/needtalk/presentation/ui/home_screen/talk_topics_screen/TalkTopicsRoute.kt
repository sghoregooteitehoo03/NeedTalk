package com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.UiScreen
import kotlinx.coroutines.flow.collectLatest
import kotlinx.serialization.json.Json

@Composable
fun TalkTopicsRoute(
    userData: UserData?,
    viewModel: TalkTopicsViewModel = hiltViewModel(),
    navigateToOther: (route: String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(
        viewModel.uiEvent
    ) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is TalkTopicsUiEvent.ClickAddTopic -> {
                    navigateToOther(UiScreen.AddTalkTopicScreen.route)
                }

                is TalkTopicsUiEvent.ClickTopicCategory -> {
                    navigateToDetailScreen(
                        type = TalkTopicsDetailType.CategoryType(
                            categoryCode = event.category.code,
                            userId = userData?.userId ?: "",
                            _title = event.category.title
                        ),
                        navigateToOther = navigateToOther
                    )
                }

                is TalkTopicsUiEvent.ClickPopularMore -> {
                    navigateToDetailScreen(
                        type = TalkTopicsDetailType.PopularType(
                            index = 0,
                            userId = userData?.userId ?: "",
                            _title = "인기 대화주제"
                        ),
                        navigateToOther = navigateToOther
                    )
                }

                is TalkTopicsUiEvent.ClickTalkTopic -> {
                    navigateToDetailScreen(
                        type = TalkTopicsDetailType.PopularType(
                            index = event.index,
                            userId = userData?.userId ?: "",
                            _title = "인기 대화주제"
                        ),
                        navigateToOther = navigateToOther
                    )
                }

                is TalkTopicsUiEvent.ClickGroupMore -> {

                }


                is TalkTopicsUiEvent.ClickGroup -> {

                }
            }
        }
    }

    TalkTopicsScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}

private fun navigateToDetailScreen(
    type: TalkTopicsDetailType,
    navigateToOther: (route: String) -> Unit
) {
    val detailTypeJson = Json.encodeToString(TalkTopicsDetailType.serializer(), type)
    val route = UiScreen.TalkTopicsDetailScreen.route + "?type=${detailTypeJson}"

    navigateToOther(route)
}
