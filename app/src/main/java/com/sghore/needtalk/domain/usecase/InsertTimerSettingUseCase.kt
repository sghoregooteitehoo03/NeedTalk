package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.repository.TalkRepository
import javax.inject.Inject

class InsertTimerSettingUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {

    suspend operator fun invoke(timerSetting: TimerSettingEntity) {
        talkRepository.insertTimerSettingEntity(timerSetting)
    }
}