package com.sghore.needtalk.presentation.ui.profile_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.UserData
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
                is ProfileUiEvent.ClickRemoveProfile -> TODO()
            }
        }
    }

    ProfileScreen(
        userData = userData,
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}