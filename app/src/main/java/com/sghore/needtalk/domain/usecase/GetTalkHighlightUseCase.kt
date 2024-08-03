package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.domain.model.TalkHighlight
import kotlinx.coroutines.flow.map
import java.io.File
import javax.inject.Inject

class GetTalkHighlightUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    operator fun invoke(talKHistoryId: String) =
        talkRepository.getTalkHighlightEntities(talKHistoryId).map {
            it.map { talkHighlightEntity ->
                TalkHighlight(
                    id = talkHighlightEntity?.id,
                    title = talkHighlightEntity?.title ?: "",
                    file = File(talkHighlightEntity?.filePath ?: ""),
                    duration = talkHighlightEntity?.duration ?: 0
                )
            }
        }
}