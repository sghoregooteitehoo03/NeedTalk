package com.sghore.needtalk.presentation.ui.create_profile_screen

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.R
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.domain.usecase.InsertUserEntity2UseCase
import com.sghore.needtalk.util.Constants
import com.sghore.needtalk.util.mergeImages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateProfileViewModel @Inject constructor(
    private val insertUserEntity2UseCase: InsertUserEntity2UseCase,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
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

    val faceImageResources = listOf(
        R.drawable.face1,
        R.drawable.face2,
        R.drawable.face3,
        R.drawable.face4,
        R.drawable.face5,
        R.drawable.face6,
        R.drawable.face7,
    )
    val hairStyleImageResources = listOf(
        R.drawable.none,
        R.drawable.hair1,
        R.drawable.hair2,
        R.drawable.hair3,
        R.drawable.hair4,
        R.drawable.hair5,
        R.drawable.hair6,
        R.drawable.hair7,
        R.drawable.hair8,
        R.drawable.hair9,
        R.drawable.hair10,
        R.drawable.hair11,
        R.drawable.hair12,
        R.drawable.hair13,
        R.drawable.hair14,
    )
    val accessoryImageResources = listOf(
        R.drawable.none,
        R.drawable.earring,
        R.drawable.necklace,
        R.drawable.glasses,
        R.drawable.glasses2,
        R.drawable.sunglasses,
        R.drawable.ribbon,
        R.drawable.mask,
        R.drawable.smoke,
        R.drawable.earphone,
        R.drawable.headphone,
        R.drawable.hairband,
    )

    init {
        val userId = savedStateHandle.get<String>("userId")
        if (userId != null) {
            viewModelScope.launch {
                val userEntity = userRepository
                    .getUserEntity(userId)
                    .first()

                if (userEntity != null) {
                    _uiState.update {
                        it.copy(
                            profileName = userEntity.name,
                            selectedFaceIndex = faceImageResources.indexOf(userEntity.selectedFaceImageRes),
                            selectedHairStyleIndex = hairStyleImageResources.indexOf(userEntity.selectedHairImageRes),
                            selectedAccessoryIndex = accessoryImageResources.indexOf(userEntity.selectedAccessoryImageRes),
                            isUpdateProfile = true
                        )
                    }
                }
            }
        }
    }

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
        onUpdateUserData: (UserData, Boolean) -> Unit
    ) {
        viewModelScope.launch {
            val hairSelectedIndex = uiState.value.selectedHairStyleIndex
            val accessorySelectedIndex = uiState.value.selectedAccessoryIndex
            val mergedProfileImage = if (hairSelectedIndex == 0 && accessorySelectedIndex == 0) {
                mergeImages(listOf(faceImage))
            } else if (hairSelectedIndex == 0) {
                mergeImages(listOf(faceImage, accessoryImage))
            } else if (accessorySelectedIndex == 0) {
                mergeImages(listOf(faceImage, hairImage))
            } else {
                mergeImages(listOf(faceImage, hairImage, accessoryImage))
            }
            val createUserData = UserData(
                userId = userId,
                name = _uiState.value.profileName,
                profileImage = mergedProfileImage.asImageBitmap(),
                experiencePoint = Constants.MAX_EXPERIENCE_POINT,
                friendshipPoint = Constants.MAX_FRIENDSHIP_POINT
            )

            insertUserEntity2UseCase(
                userData = createUserData,
                selectedFaceImageRes = faceImageResources[uiState.value.selectedFaceIndex],
                selectedHairImageRes = hairStyleImageResources[uiState.value.selectedHairStyleIndex],
                selectedAccessoryImageRes = accessoryImageResources[uiState.value.selectedAccessoryIndex]
            )

            // 프로필 수정일 때
            if (uiState.value.isUpdateProfile) {
                onUpdateUserData(createUserData, false)
            } else { // 프로필 생성 일 떄
                onUpdateUserData(createUserData, true)
            }
        }
    }

    fun handelEvent(event: CreateProfileUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }
}