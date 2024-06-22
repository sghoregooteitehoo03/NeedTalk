package com.sghore.needtalk.presentation.ui.create_talk_screen

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import com.sghore.needtalk.presentation.ui.theme.Orange50
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateTalkRoute(
    viewModel: CreateTalkViewModel = hiltViewModel(),
    userData: UserData?,
    navigateUp: () -> Unit,
    navigateToTimer: (TimerCommunicateInfo) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )
    val systemUiController = rememberSystemUiController()
    val context = LocalContext.current

    DisposableEffectWithLifeCycle(
        onCreate = {
            systemUiController.setStatusBarColor(
                color = Orange50,
                darkIcons = false
            )
        },
        onDispose = {}
    )

    LaunchedEffect(key1 = viewModel.uiEvent, block = {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CreateTalkUiEvent.ClickBackArrow -> {
                    navigateUp()
                }

                is CreateTalkUiEvent.ClickComplete -> {
                    viewModel.completeTimerSetting(navigateToTimer)
                }

                is CreateTalkUiEvent.ClickStopWatchMode -> {
                    viewModel.setTimerAllow(event.isAllow)
                }

                is CreateTalkUiEvent.ClickNumberOfPeople -> {
                    viewModel.changeNumberOfPeople(event.number)
                }

                is CreateTalkUiEvent.ErrorMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    })

    CreateTalkScreen(
        userData = userData,
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}