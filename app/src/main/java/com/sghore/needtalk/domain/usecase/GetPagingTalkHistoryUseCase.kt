package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkRepository
import javax.inject.Inject

class GetPagingTalkHistoryUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    operator fun invoke() =
        talkRepository.getPagingTalkHistory()
}