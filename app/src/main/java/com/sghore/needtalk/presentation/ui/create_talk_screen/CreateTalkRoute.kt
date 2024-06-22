package com.sghore.needtalk.presentation.ui.create_talk_screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DialogTalkTopics
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import com.sghore.needtalk.presentation.ui.theme.Orange
import com.sghore.needtalk.presentation.ui.theme.Orange50
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateRoute(
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

                is CreateTalkUiEvent.ChangeTime -> {
                    viewModel.changeTalkTime(event.talkTime)
                }

                is CreateTalkUiEvent.ClickStopWatchMode -> {
                    viewModel.stopwatchOnOff(event.isAllow)
                }

                is CreateTalkUiEvent.ClickNumberOfPeople -> {
                    viewModel.changeNumberOfPeople(event.number)
                }

                is CreateTalkUiEvent.ClickTopicCategory -> {
                    viewModel.setDialogScreen(
                        DialogScreen.DialogTalkTopics(
                            event.topicCategory,
                            event.groupCode
                        )
                    )
                }

                is CreateTalkUiEvent.ClickAddTopic -> {
                    viewModel.setDialogScreen(DialogScreen.DialogAddTopic)
                }

                is CreateTalkUiEvent.ClickRemoveTopic -> {
                    viewModel.insertOrRemoveTalkTopic(
                        talkTopicEntity = event.talkTopicEntity,
                        isRemove = true
                    )
                }

                is CreateTalkUiEvent.ErrorMessage -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    })

    CreateScreen(
        userData = userData,
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
//    Surface {
//
//
//        when (val dialog = uiState.dialogScreen) {
//            is DialogScreen.DialogTalkTopics -> {
//                var talkTopics by remember {
//                    mutableStateOf(listOf<TalkTopicEntity>())
//                }
//
//                if (talkTopics.isEmpty()) {
//                    viewModel.getTalkTopics(
//                        groupCode = dialog.groupCode,
//                        updateTopics = {
//                            talkTopics = it
//                        }
//                    )
//                }
//
//                DialogTalkTopics(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colors.background,
//                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
//                        )
//                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
//                        .padding(14.dp),
//                    onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
//                    topicCategory = dialog.topicCategory,
//                    talkTopics = talkTopics,
//                    talkTopicItem = { talkTopicEntity ->
//                        TalkTopicItem(
//                            talkTopicEntity = talkTopicEntity,
//                            onDeleteTopic = {
//                                viewModel.insertOrRemoveTalkTopic(
//                                    talkTopicEntity = it,
//                                    isRemove = true
//                                )
//                            }
//                        )
//                    },
//                )
//            }
//
//            is DialogScreen.DialogAddTopic -> {
//                DialogAddTopic(
//                    modifier = Modifier
//                        .background(
//                            color = MaterialTheme.colors.background,
//                            shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
//                        )
//                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
//                        .padding(14.dp),
//                    onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
//                    onAddClick = {
//                        viewModel.insertOrRemoveTalkTopic(
//                            talkTopicEntity = it,
//                            isRemove = false
//                        )
//                    }
//                )
//            }
//
//            else -> {}
//        }
//    }
}