package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkRepository
import kotlinx.coroutines.flow.filter
import javax.inject.Inject

class GetUserEntityUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    operator fun invoke(userId: String) =
        talkRepository.getUserEntity(userId)
}