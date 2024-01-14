package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.TalkRepository
import javax.inject.Inject

class InsertUserEntityUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    suspend operator fun invoke(userEntity: UserEntity) =
        talkRepository.insertUserEntity(userEntity)
}