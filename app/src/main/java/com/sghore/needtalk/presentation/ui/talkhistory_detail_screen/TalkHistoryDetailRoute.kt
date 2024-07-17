package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun TalkHistoryDetailRoute(
    viewModel: TalkHistoryDetailViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    navigateToAddHighlightScreen: (String, List<Int>) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    DisposableEffectWithLifeCycle(
        onResume = {
            if (uiState.talkHistory != null) {
                viewModel.preparePlayer(uiState.talkHistory?.recordFile?.path ?: "")
            }
        },
        onStop = {
            viewModel.pauseRecord()
        },
        onDispose = { viewModel.finishPlayer() }
    )

    LaunchedEffect(viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is TalkHistoryDetailUiEvent.ClickNavigateUp -> navigateUp()
                is TalkHistoryDetailUiEvent.OptionInfo ->
                    viewModel.setDialogScreen(DialogScreen.DialogFileInfo)

                is TalkHistoryDetailUiEvent.OptionRenameTitle ->
                    viewModel.setDialogScreen(DialogScreen.DialogRenameTitle)

                is TalkHistoryDetailUiEvent.OptionRemoveTalkHistory ->
                    viewModel.setDialogScreen(DialogScreen.DialogRemoveTalkHistory)

                is TalkHistoryDetailUiEvent.ChangeTime -> viewModel.changeTime(event.time)
                is TalkHistoryDetailUiEvent.SeekPlayer -> viewModel.seekPlayer(event.isSeeking)
                is TalkHistoryDetailUiEvent.ClickBeforeSecond -> viewModel.jumpToSecond(-5000)
                is TalkHistoryDetailUiEvent.ClickAfterSecond -> viewModel.jumpToSecond(5000)
                is TalkHistoryDetailUiEvent.ClickPlayOrPause -> {
                    if (event.isPlay) {
                        viewModel.playRecord()
                    } else {
                        viewModel.pauseRecord()
                    }
                }

                is TalkHistoryDetailUiEvent.ClickClips -> TODO()
                is TalkHistoryDetailUiEvent.ClickMakeClip ->
                    navigateToAddHighlightScreen(
                        uiState.talkHistory?.recordFile?.path ?: "",
                        uiState.talkHistory?.recordAmplitude ?: emptyList()
                    )
            }
        }
    }

    Surface {
        TalkHistoryDetailScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent
        )

        when (uiState.dialogScreen) {
            is DialogScreen.DialogFileInfo -> {
                FileInfoDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .padding(14.dp),
                    onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                    talkHistory = uiState.talkHistory,
                )
            }

            is DialogScreen.DialogRenameTitle -> {
                RenameTitleDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .padding(14.dp),
                    onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                    title = uiState.talkHistory?.talkTitle ?: "",
                    onTitleChange = { viewModel.updateTitle(it) }
                )
            }

            is DialogScreen.DialogRemoveTalkHistory -> {
                RemoveTalkHistoryDialog(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .padding(14.dp),
                    onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                    onClickRemove = {
                        viewModel.removeTalkHistory()
                        navigateUp()
                    }
                )
            }

            else -> {}
        }
    }
}