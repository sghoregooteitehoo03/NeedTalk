package com.sghore.needtalk.domain.usecase

import androidx.compose.ui.graphics.asAndroidBitmap
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.bitmapToByteArray
import javax.inject.Inject

class InsertUserEntityUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        userData: UserData,
        selectedFaceImageRes: Int,
        selectedHairImageRes: Int,
        selectedAccessoryImageRes: Int
    ) {
        val userEntity = UserEntity(
            userId = userData.userId,
            name = userData.name,
            profileImage = bitmapToByteArray(userData.profileImage.asAndroidBitmap()),
            selectedFaceImageRes = selectedFaceImageRes,
            selectedHairImageRes = selectedHairImageRes,
            selectedAccessoryImageRes = selectedAccessoryImageRes
        )

        userRepository.insertUserEntity(userEntity)
    }
}