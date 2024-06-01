package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.UserEntity2
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.bitmapToByteArray
import javax.inject.Inject

class InsertUserEntity2UseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userData: UserData) {
        val userEntity = UserEntity2(
            userId = userData.userId,
            name = userData.name,
            profileImage = bitmapToByteArray(userData.profileImage)
        )

        userRepository.insertUserEntity(userEntity)
    }
}