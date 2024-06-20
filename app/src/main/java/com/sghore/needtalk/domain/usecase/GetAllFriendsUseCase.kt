package com.sghore.needtalk.domain.usecase

import androidx.compose.ui.graphics.asImageBitmap
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.Friend
import com.sghore.needtalk.util.byteArrayToBitmap
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllFriendsUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke() = userRepository.getAllFriendEntities()
        .map { entities ->
            entities.map { entity ->
                Friend(
                    userId = entity.userId,
                    name = entity.userName,
                    profileImage = byteArrayToBitmap(entity.profileImage).asImageBitmap(),
                    experiencePoint = entity.experiencePoint,
                    friendshipPoint = entity.friendshipPoint
                )
            }
        }
}