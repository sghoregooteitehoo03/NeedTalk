package com.sghore.needtalk.domain.usecase

import androidx.compose.ui.graphics.asImageBitmap
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.byteArrayToBitmap
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllUserDataUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() = userRepository.getAllFriendEntities()
        .map { entities ->
            entities.map { entity ->
                val userEntity = userRepository.getUserEntity(entity.userId).first()!!
                UserData(
                    userId = entity.userId,
                    name = userEntity.name,
                    profileImage = byteArrayToBitmap(userEntity.profileImage).asImageBitmap(),
                    experiencePoint = entity.experiencePoint,
                    friendshipPoint = entity.friendshipPoint
                )
            }
        }
}