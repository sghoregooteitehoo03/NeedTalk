package com.sghore.needtalk.presentation.ui.profile_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.domain.model.UserData

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    userData: UserData?
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    ProfileScreen(
        userData = userData,
        uiState = uiState
    )
}