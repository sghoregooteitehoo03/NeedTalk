package com.sghore.needtalk.presentation.ui.create_profile_screen

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.domain.usecase.InsertUserEntity2UseCase
import com.sghore.needtalk.util.mergeImages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val insertUserEntity2UseCase: InsertUserEntity2UseCase
) : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(CreateProfileUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        CreateProfileUiState()
    )

    // UI Event
    private val _uiEvent = MutableSharedFlow<CreateProfileUiEvent>()
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    // 닉네임 변경 이벤트 처리
    fun onNameChanged(name: String) {
        _uiState.update { it.copy(profileName = name) }
    }

    // 선택한 프로필의 이미지 인덱스 처리
    fun onChangeProfile(profileType: ProfileType, selectedProfileIndex: Int) {
        when (profileType) {
            ProfileType.Face -> {
                _uiState.update { it.copy(selectedFaceIndex = selectedProfileIndex) }
            }

            ProfileType.Hair -> {
                _uiState.update { it.copy(selectedHairStyleIndex = selectedProfileIndex) }
            }

            ProfileType.Accessory -> {
                _uiState.update { it.copy(selectedAccessoryIndex = selectedProfileIndex) }
            }
        }
    }

    fun insertUserData(
        userId: String,
        faceImage: Bitmap,
        hairImage: Bitmap,
        accessoryImage: Bitmap,
        onUpdateUserData: (UserData) -> Unit
    ) {
        viewModelScope.launch {
            val mergedProfileImage = mergeImages(listOf(faceImage, hairImage, accessoryImage))
            val createUserData = UserData(
                userId = userId,
                name = _uiState.value.profileName,
                profileImage = mergedProfileImage.asImageBitmap()
            )

            insertUserEntity2UseCase(createUserData)
            onUpdateUserData(createUserData)
        }
    }

    fun handelEvent(event: CreateProfileUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}