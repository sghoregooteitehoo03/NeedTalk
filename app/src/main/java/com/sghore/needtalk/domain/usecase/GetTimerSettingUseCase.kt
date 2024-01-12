package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.MusicEntity
import com.sghore.needtalk.data.repository.TalkRepository
import com.sghore.needtalk.presentation.ui.create_screen.CreateUiState
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetTimerSettingUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {

    suspend operator fun invoke(): CreateUiState {
        val timerSettingEntity = talkRepository.getTimerSettingEntity().first()
        val musicEntities = talkRepository.getAllMusicEntity().first()
        val defaultMusics = listOf(
            MusicEntity(
                "",
                thumbnailImage = "",
                "음악 없음",
                timestamp = 0L
            )
        )

        return if (timerSettingEntity != null) {
            CreateUiState(
                talkTime = timerSettingEntity.talkTime,
                isStopwatch = timerSettingEntity.isStopwatch,
                musics = defaultMusics + musicEntities,
                initialMusicId = timerSettingEntity.selectMusicId,
                allowRepeatMusic = timerSettingEntity.allowRepeatMusic,
                numberOfPeople = timerSettingEntity.numberOfPeople
            )
        } else {
            CreateUiState(musics = defaultMusics + musicEntities)
        }
    }
}