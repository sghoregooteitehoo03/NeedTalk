package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TalkHistoryEntity
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.domain.model.TalkHistory
import java.nio.ByteBuffer
import javax.inject.Inject

class InsertTalkHistoryUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    suspend operator fun invoke(talkHistory: TalkHistory?) {
        if (talkHistory != null) {
            val insertEntity = TalkHistoryEntity(
                id = talkHistory.id,
                talkTitle = talkHistory.talkTitle,
                talkTime = talkHistory.talkTime,
                recordAmplitude = intListToByteArray(talkHistory.recordAmplitude),
                recordFilePath = talkHistory.recordFile?.path ?: "",
                createTimeStamp = talkHistory.createTimeStamp
            )

            talkRepository.insertTalkHistoryEntity(talkHistoryEntity = insertEntity)
        }
    }

    private fun intListToByteArray(intList: List<Int>): ByteArray {
        val byteBuffer = ByteBuffer.allocate(intList.size * 4) // 각 Int는 4바이트
        intList.forEach { byteBuffer.putInt(it) }
        return byteBuffer.array()
    }
}