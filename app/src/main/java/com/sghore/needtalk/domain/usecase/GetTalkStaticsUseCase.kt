package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.domain.model.TalkStatics
import javax.inject.Inject

class GetTalkStaticsUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {

    suspend operator fun invoke(
        currentUser: UserEntity,
        startTime: Long,
        endTime: Long
    ): TalkStatics {
        val talkHistory = talkRepository.getTalkEntities(startTime, endTime).map { talkEntity ->
            TalkHistory(
                talkTime = talkEntity.talkTime,
                users = talkEntity.usersId.split(",")
                    .map { userId ->
                        UserEntity(userId = userId.trim(), name = "", color = 0)
                    },
                createTimeStamp = talkEntity.createTimeStamp
            )
        }

        return talkRepository.getTalkStatics(
            currentUser = currentUser,
            talkHistory = talkHistory,
            count = 3
        )
    }
}