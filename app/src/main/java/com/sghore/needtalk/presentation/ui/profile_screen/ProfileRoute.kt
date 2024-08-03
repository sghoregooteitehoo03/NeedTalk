package com.sghore.needtalk.presentation.ui.profile_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.ConfirmWithCancelDialog
import com.sghore.needtalk.presentation.ui.DialogScreen
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    userData: UserData?,
    navigateUp: () -> Unit,
    navigateToCreateProfile: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(key1 = viewModel.uiEvent) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is ProfileUiEvent.ClickNavigateUp -> navigateUp()
                is ProfileUiEvent.ClickEditProfile -> navigateToCreateProfile()
                is ProfileUiEvent.ClickRemoveFriend -> viewModel.setDialogScreen(
                    DialogScreen.DialogRemoveFriend(event.friend)
                )
            }
        }
    }

    ProfileScreen(
        userData = userData,
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )

    when (val dialogScreen = uiState.dialogScreen) {
        is DialogScreen.DialogRemoveFriend -> {
            ConfirmWithCancelDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(14.dp),
                onDismiss = { viewModel.setDialogScreen(DialogScreen.DialogDismiss) },
                title = "친구 삭제",
                message = "\"${dialogScreen.friend.name}\"을(를)\n친구 목록에서 삭제하시겠습니까?",
                confirmText = "삭제하기",
                cancelText = "취소",
                onConfirm = {
                    viewModel.removeFriend(dialogScreen.friend.userId)
                }
            )
        }

        else -> {}
    }
}