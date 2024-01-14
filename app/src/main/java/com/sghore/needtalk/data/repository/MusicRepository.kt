package com.sghore.needtalk.data.repository

import com.sghore.needtalk.data.model.dto.VideoDTO
import com.sghore.needtalk.data.model.entity.MusicEntity
import com.sghore.needtalk.data.repository.database.TalkDao
import com.sghore.needtalk.data.repository.retrofit.RetrofitService
import com.sghore.needtalk.util.Constants
import retrofit2.Retrofit
import javax.inject.Inject

class MusicRepository @Inject constructor(
    private val dao: TalkDao,
    private val retrofitBuilder: Retrofit.Builder
) {

    // 유튜브에서 영상 정보를 가져옴
    suspend fun getVideoInfo(videoId: String): VideoDTO {
        val retrofitService = getRetrofitService()

        return retrofitService.getVideoInfo(videoId)
    }

    // 저장된 음악정보 리스트 반환
    fun getAllMusicEntity() =
        dao.getAllMusicEntity()

    // 음악정보 삽입
    suspend fun insertMusicEntity(musicEntity: MusicEntity) =
        dao.insertMusicEntity(musicEntity)

    // 음악정보 삭제
    suspend fun removeMusicEntity(id: String) =
        dao.removeMusicEntity(id)

    private fun getRetrofitService() =
        retrofitBuilder.baseUrl(Constants.YOUTUBE_API_BASE_URL)
            .build()
            .create(RetrofitService::class.java)
}