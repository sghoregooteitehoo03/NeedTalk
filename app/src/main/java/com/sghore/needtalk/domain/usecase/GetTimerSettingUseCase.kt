package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.MusicEntity
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.repository.MusicRepository
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.presentation.ui.create_screen.CreateUiState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTimerSettingUseCase @Inject constructor(
    private val talkRepository: TalkRepository,
    private val musicRepository: MusicRepository
) {

    operator fun invoke(transform: suspend (TimerSettingEntity?, List<MusicEntity>) -> Unit) =
        combine(
            talkRepository.getTimerSettingEntity(),
            musicRepository.getAllMusicEntity(),
            transform
        )
}