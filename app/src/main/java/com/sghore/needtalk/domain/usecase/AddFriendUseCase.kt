package com.sghore.needtalk.domain.usecase

import androidx.compose.ui.graphics.asAndroidBitmap
import com.sghore.needtalk.data.model.entity.FriendEntity
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.Friend
import com.sghore.needtalk.util.bitmapToByteArray
import javax.inject.Inject

class AddFriendUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(friend: Friend) {
        val createdFriend = FriendEntity(
            userId = friend.userId,
            userName = friend.name,
            profileImage = bitmapToByteArray(friend.profileImage.asAndroidBitmap()),
            experiencePoint = friend.experiencePoint,
            friendshipPoint = friend.friendshipPoint
        )

        userRepository.insertFriendEntity(createdFriend)
    }
}