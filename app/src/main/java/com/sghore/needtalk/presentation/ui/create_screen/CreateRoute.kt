package com.sghore.needtalk.presentation.ui.create_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.presentation.ui.DialogScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateRoute(
    viewModel: CreateViewModel = hiltViewModel(),
    navigateUp: () -> Unit,
    navigateToTimer: (TimerCommunicateInfo) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(key1 = viewModel.uiEvent, block = {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is CreateUiEvent.ClickBackArrow -> {
                    navigateUp()
                }

                is CreateUiEvent.ClickComplete -> {
                    viewModel.insertTimerSetting(navigateToTimer)
                }

                is CreateUiEvent.ChangeTime -> {
                    viewModel.changeTalkTime(event.talkTime)
                }

                is CreateUiEvent.ClickStopWatchMode -> {
                    viewModel.stopwatchOnOff(event.isAllow)
                }

                is CreateUiEvent.ClickNumberOfPeople -> {
                    viewModel.changeNumberOfPeople(event.number)
                }

                is CreateUiEvent.ClickTopicCategory -> {
                    viewModel.setDialogScreen(
                        DialogScreen.DialogTalkTopics(
                            event.topicCategory,
                            event.groupCode
                        )
                    )
                }

                is CreateUiEvent.ClickAddTopic -> {
                    viewModel.setDialogScreen(DialogScreen.DialogAddTopic)
                }

                is CreateUiEvent.ClickRemoveTopic -> {
                    viewModel.insertOrRemoveTalkTopic(
                        talkTopicEntity = event.talkTopicEntity,
                        isRemove = true
                    )
                }

                is CreateUiEvent.ErrorMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    })

    Surface {
        CreateScreen(
            uiState = uiState,
            onEvent = viewModel::handelEvent
        )

        when (val dialog = uiState.dialogScreen) {
            is DialogScreen.DialogTalkTopics -> {
                DialogTalkTopics(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .padding(14.dp),
                    onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                    topicCategory = dialog.topicCategory,
                    groupCode = dialog.groupCode,
                    talkTopics = listOf(),
                    onDeleteTopic = {}
                )
            }

            is DialogScreen.DialogAddTopic -> {
                DialogAddTopic(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                        .padding(14.dp),
                    onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                    onAddClick = {
                        viewModel.insertOrRemoveTalkTopic(
                            talkTopicEntity = it,
                            isRemove = false
                        )
                    }
                )
            }

            else -> {}
        }
    }
}