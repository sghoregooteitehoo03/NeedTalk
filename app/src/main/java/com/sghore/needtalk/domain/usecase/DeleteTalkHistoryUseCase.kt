package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.domain.model.TalkHistory
import java.io.File
import javax.inject.Inject

class DeleteTalkHistoryUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    suspend operator fun invoke(talkHistory: TalkHistory) {
        val recordFile = talkHistory.recordFile
        if (recordFile?.exists() == true) {
            recordFile.delete()
        }

        talkRepository.deleteTalkHistoryEntity(talkHistory.id)
        talkRepository.deleteTalkHistoryParticipantEntities(talkHistory.id)
    }
}