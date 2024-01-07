package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.TalkEntity
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.domain.model.TalkHistory
import javax.inject.Inject

class InsertTalkEntityUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    suspend operator fun invoke(talkHistory: TalkHistory) {
        val talkEntity = TalkEntity(
            talkTime = talkHistory.talkTime,
            usersId = talkHistory.users.joinToString { (it?.userId ?: "") },
            createTimeStamp = talkHistory.createTimeStamp
        )

        talkRepository.insertTalkEntity(talkEntity)
    }
}