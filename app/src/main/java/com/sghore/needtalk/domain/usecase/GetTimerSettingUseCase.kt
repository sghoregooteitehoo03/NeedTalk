package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.repository.TalkRepository
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetTimerSettingUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {

    operator fun invoke() =
        talkRepository.getTimerSettingEntity()
}