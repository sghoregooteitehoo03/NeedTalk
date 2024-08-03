package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkRepository
import java.io.File
import javax.inject.Inject

class DeleteTalkHighlightUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    suspend operator fun invoke(
        talkHighlightId: Int?,
        recordFile: File
    ) {
        recordFile.delete()
        talkRepository.deleteTalkHighlightEntity(talkHighlightId)
    }
}