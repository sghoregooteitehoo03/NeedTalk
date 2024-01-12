package com.sghore.needtalk.presentation.ui.create_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateRoute(
    viewModel: CreateViewModel = hiltViewModel(),
    navigateUp: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

                }

                is CreateUiEvent.ClickAllowRepeatMusic -> {
                    viewModel.repeatMusicOnOff(event.isAllow)
                }

                is CreateUiEvent.ClickNumberOfPeople -> {
                    viewModel.changeNumberOfPeople(event.number)
                }
            }
        }
    })

    CreateScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}