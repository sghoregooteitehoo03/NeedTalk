package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.FriendEntity
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.UserData
import javax.inject.Inject

class AddFriendUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(friend: UserData) {
        val createdFriend = FriendEntity(
            userId = friend.userId,
            experiencePoint = 0,
            friendshipPoint = 0
        )

        userRepository.insertFriendEntity(createdFriend)
    }
}