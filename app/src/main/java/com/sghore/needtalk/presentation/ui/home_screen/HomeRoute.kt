package com.sghore.needtalk.presentation.ui.home_screen

import android.annotation.SuppressLint
import android.provider.Settings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("HardwareIds")
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel(),
    navigateToCreateScreen: () -> Unit,
    updateUserEntity: (UserEntity?) -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagingItems = uiState.talkHistory?.collectAsLazyPagingItems()

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is HomeUiEvent.UpdateUserEntity -> {
                    updateUserEntity(event.userEntity)
                }

                is HomeUiEvent.ClickNameTag -> {
                    viewModel.setDialogScreen(DialogScreen.DialogSetName)
                }

                is HomeUiEvent.ClickStartAndClose -> {
                    viewModel.clickStartAndClose()
                }

                is HomeUiEvent.ClickCreate -> {
                    viewModel.clickStartAndClose()
                    navigateToCreateScreen()
                }

                is HomeUiEvent.SuccessUpdateUserName -> {
                    pagingItems?.refresh()
                    viewModel.setDialogScreen(DialogScreen.DialogDismiss)
                }
            }
        }
    }

    DisposableEffectWithLifeCycle(
        onCreate = {
            val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            viewModel.initUser(id)
        },
        onDispose = {}
    )

    Surface {
        HomeScreen(
            uiState = uiState,
            pagingItems = pagingItems,
            onEvent = viewModel::handelEvent
        )

        when (uiState.dialogScreen) {
            is DialogScreen.DialogSetName -> {
                BottomSheetDialog(
                    onDismissRequest = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                ) {
                    SetName(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = MaterialTheme.colors.background,
                                shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                            )
                            .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                            .padding(14.dp),
                        userName = uiState.user?.name ?: "",
                        onCloseClick = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                        onEditClick = viewModel::updateUserName
                    )
                }
            }

            else -> {}
        }
    }
}