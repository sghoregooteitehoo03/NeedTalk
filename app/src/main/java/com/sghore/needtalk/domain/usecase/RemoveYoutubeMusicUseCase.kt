package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.MusicRepository
import javax.inject.Inject

class RemoveYoutubeMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(id: String) {
        musicRepository.removeMusicEntity(id)
    }
}