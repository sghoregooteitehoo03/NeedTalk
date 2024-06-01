package com.sghore.needtalk.presentation.ui.create_profile_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.join_screen.JoinUiEvent
import kotlinx.coroutines.flow.collectLatest

@Composable
fun CreateProfileRoute(
    viewModel: CreateProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle(
        lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    )

    LaunchedEffect(
        key1 = viewModel.uiEvent,
        block = {
            viewModel.uiEvent.collectLatest { event ->
                when (event) {
                    is CreateProfileUiEvent.ChangeName -> { // 닉네임 변경
                        viewModel.onNameChanged(event.name)
                    }

                    is CreateProfileUiEvent.SelectProfileImage -> { // 프로필 이미지 선택
                        viewModel.onChangeProfile(event.type, event.imageIndex)
                    }

                    is CreateProfileUiEvent.ClickConfirm -> { // 확인 버튼 클릭
                        viewModel.insertUserData(
                            userId = event.userId,
                            faceImage = event.faceImage,
                            hairImage = event.hairImage,
                            accessoryImage = event.accessoryImage
                        )
                    }
                }
            }
        }
    )

    CreateProfileScreen(
        uiState = uiState,
        onEvent = viewModel::handelEvent
    )
}