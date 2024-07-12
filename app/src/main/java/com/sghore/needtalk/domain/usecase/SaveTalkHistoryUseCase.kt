package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TalkHistoryEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryParticipantEntity
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.util.generateTalkTopicId
import java.nio.ByteBuffer
import javax.inject.Inject

class SaveTalkHistoryUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    suspend operator fun invoke(
        talkTitle: String,
        talkTime: Long,
        filePath: String,
        recordAmplitude: List<Int>,
        otherUsers: List<UserData?>
    ) {
        val currentTime = System.currentTimeMillis()
        val talkHistoryId = generateTalkTopicId("", currentTime)

        // 대화기록 저장
        val talkHistoryEntity = TalkHistoryEntity(
            id = talkHistoryId,
            talkTitle = talkTitle,
            talkTime = talkTime,
            recordAmplitude = intListToByteArray(recordAmplitude),
            recordFilePath = filePath
        )
        talkRepository.insertTalkHistoryEntity(talkHistoryEntity)

        // 대화에 참여한 참여자 기록 저장
        otherUsers.forEach {
            if (it != null) {
                val talkHistoryParticipantEntity = TalkHistoryParticipantEntity(
                    talkHistoryId = talkHistoryId,
                    userId = it.userId,
                    friendshipPoint = it.friendshipPoint
                )
                talkRepository.insertTalkHistoryParticipantEntity(talkHistoryParticipantEntity)
            }
        }
    }

    private fun intListToByteArray(intList: List<Int>): ByteArray {
        val byteBuffer = ByteBuffer.allocate(intList.size * 4) // 각 Int는 4바이트
        intList.forEach { byteBuffer.putInt(it) }
        return byteBuffer.array()
    }
}