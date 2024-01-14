package com.sghore.needtalk.presentation.ui.create_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.DialogScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateRoute(
    viewModel: CreateViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.uiEvent, block = {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CreateUiEvent.ClickBackArrow -> {
                    navigateUp()
                }

                is CreateUiEvent.ChangeTime -> {
                    viewModel.changeTalkTime(event.talkTime)
                }

                is CreateUiEvent.ClickStopWatchMode -> {
                    viewModel.stopwatchOnOff(event.isAllow)
                }

                is CreateUiEvent.ClickAddMusic -> {
                    viewModel.setDialogScreen(DialogScreen.DialogCreateMusic)
                }

                is CreateUiEvent.ClickRemoveMusic -> {
                    viewModel.setDialogScreen(DialogScreen.DialogRemoveMusic(event.musicEntity))
                }

                is CreateUiEvent.ClickAllowRepeatMusic -> {
                    viewModel.repeatMusicOnOff(event.isAllow)
                }

                is CreateUiEvent.ClickNumberOfPeople -> {
                    viewModel.changeNumberOfPeople(event.number)
                }

                is CreateUiEvent.SuccessInsertMusic, CreateUiEvent.SuccessRemoveMusic -> {
                    viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                }

                is CreateUiEvent.FailInsertMusic -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    })

    CreateScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )

    when (uiState.dialogScreen) {
        is DialogScreen.DialogCreateMusic -> {
            val isLoading = uiState.isLoading

            AddMusicDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .fillMaxWidth()
                    .padding(14.dp),
                onDismiss = {
                    if (!isLoading)
                        viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                },
                onClick = viewModel::addYoutubeMusic,
                isLoading = isLoading
            )
        }

        is DialogScreen.DialogRemoveMusic -> {
            val isLoading = uiState.isLoading

            RemoveMusicDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .fillMaxWidth()
                    .padding(14.dp),
                musicEntity = (uiState.dialogScreen as DialogScreen.DialogRemoveMusic).musicEntity,
                onDismiss = {
                    if (!isLoading)
                        viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                },
                isLoading = isLoading,
                onClick = viewModel::removeMusic
            )
        }

        else -> {}
    }
}