package com.sghore.needtalk.domain.usecase

import androidx.compose.ui.graphics.asImageBitmap
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.data.repository.UserRepository
import com.sghore.needtalk.domain.model.TalkHistory
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.byteArrayToBitmap
import kotlinx.coroutines.flow.first
import java.io.File
import java.nio.ByteBuffer
import javax.inject.Inject

class GetTalkHistoryUseCase @Inject constructor(
    private val talkRepository: TalkRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(talkHistoryId: String): TalkHistory? {
        val talkHistoryEntity = talkRepository.getTalkHistoryEntity(talkHistoryId).first()
        return if (talkHistoryEntity != null) {
            val users = talkRepository.getTalkHistoryParticipantEntities(talkHistoryEntity.id)
                .first()
                .map { participantEntity ->
                    val userEntity =
                        userRepository.getUserEntity(participantEntity.userId).first()!!
                    UserData(
                        userId = userEntity.userId,
                        name = userEntity.name,
                        profileImage = byteArrayToBitmap(userEntity.profileImage)
                            .asImageBitmap(),
                        experiencePoint = 0f,
                        friendshipPoint = participantEntity.friendshipPoint
                    )
                }

            val recordPath = talkHistoryEntity.recordFilePath
            TalkHistory(
                id = talkHistoryEntity.id,
                talkTitle = talkHistoryEntity.talkTitle,
                talkTime = talkHistoryEntity.talkTime,
                recordFile = if (recordPath.isEmpty()) {
                    null
                } else {
                    File(recordPath)
                },
                recordAmplitude = byteArrayToIntList(talkHistoryEntity.recordAmplitude),
                users = users,
                clipCount = 0,
                createTimeStamp = talkHistoryEntity.createTimeStamp
            )
        } else {
            null
        }
    }

    private fun byteArrayToIntList(byteArray: ByteArray): List<Int> {
        val intList = mutableListOf<Int>()
        val byteBuffer = ByteBuffer.wrap(byteArray)
        while (byteBuffer.remaining() >= 4) {
            intList.add(byteBuffer.int)
        }
        return intList
    }
}