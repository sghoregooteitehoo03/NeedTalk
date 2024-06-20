package com.sghore.needtalk.presentation.ui.add_talktopic_screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.TalkTopic
import com.sghore.needtalk.domain.model.UserData
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddTalkTopicRoute(
    viewModel: AddTalkTopicViewModel = hiltViewModel(),
    userData: UserData?,
    navigateBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is AddTalkTopicUiEvent.ChangeTalkTopicText ->
                    if (!uiState.isLoading) viewModel.changeText(event.text)

                is AddTalkTopicUiEvent.ClickAddTalkTopic -> {
                    val selectedCategories = uiState.selectedCategories.sortedBy { it.code }

                    val createTalkTopic = TalkTopic(
                        topicId = "",
                        uid = userData?.userId ?: "",
                        topic = uiState.talkTopicText,
                        favoriteCount = 0,
                        isFavorite = false,
                        isUpload = false,
                        isPublic = uiState.isPublic,
                        category1 = selectedCategories[0],
                        category2 = selectedCategories.getOrNull(1),
                        category3 = selectedCategories.getOrNull(2)
                    )

                    viewModel.insertTalkTopic(createTalkTopic)
                }

                is AddTalkTopicUiEvent.ClickNavigateBack -> navigateBack()
                is AddTalkTopicUiEvent.ClickSetPublic -> if (!uiState.isLoading) viewModel.setPublic()
                is AddTalkTopicUiEvent.ClickTalkTopicCategory ->
                    if (!uiState.isLoading) viewModel.selectCategory(event.category)

                is AddTalkTopicUiEvent.SuccessAddTalkTopic -> {
                    navigateBack()
                    Toast.makeText(context, "대화주제가 제작 되었습니다.", Toast.LENGTH_SHORT)
                        .show()
                }

                is AddTalkTopicUiEvent.FailAddTalkTopic -> {
                    Toast.makeText(context, event.errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    AddTalkTopicScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}