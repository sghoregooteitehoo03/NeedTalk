package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.model.entity.MusicEntity
import com.sghore.needtalk.data.repository.MusicRepository
import javax.inject.Inject

class AddYoutubeMusicUseCase @Inject constructor(
    private val musicRepository: MusicRepository
) {
    suspend operator fun invoke(videoId: String, title: String) {
        try {
            val videoResult = musicRepository.getVideoInfo(videoId).items[0]
            val insertMusicEntity = MusicEntity(
                id = videoResult.id,
                thumbnailImage = videoResult.snippet.thumbnails.medium.url,
                title = title,
                timestamp = System.currentTimeMillis()
            )

            musicRepository.insertMusicEntity(insertMusicEntity)
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}