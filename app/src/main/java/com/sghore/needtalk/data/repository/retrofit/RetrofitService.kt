package com.sghore.needtalk.data.repository.retrofit

import com.sghore.needtalk.BuildConfig
import com.sghore.needtalk.data.model.dto.VideoDTO
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitService {
    @GET("videos?key=${BuildConfig.YOUTUBE_API_KEY}&part=snippet")
    suspend fun getVideoInfo(@Query("id") videoId: String): VideoDTO
}