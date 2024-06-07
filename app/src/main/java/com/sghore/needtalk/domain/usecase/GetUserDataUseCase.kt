package com.sghore.needtalk.domain.usecase

import androidx.compose.ui.graphics.asImageBitmap
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.byteArrayToBitmap
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: String): UserData? {
        val userEntity = userRepository.getUserEntity(userId).first()

        return if (userEntity != null) {
            UserData(
                userId = userEntity.userId,
                name = userEntity.name,
                profileImage = byteArrayToBitmap(userEntity.profileImage).asImageBitmap()
            )
        } else {
            null
        }
    }
}