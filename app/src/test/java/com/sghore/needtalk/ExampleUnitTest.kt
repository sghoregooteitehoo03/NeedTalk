package com.sghore.needtalk

import android.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.retrofit.RetrofitService
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.presentation.ui.theme.Blue
import com.sghore.needtalk.util.Constants
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.junit.Test

import org.junit.Assert.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun getTimerTimeByStepTest() {
        val time = 256000L
        val stepTime = 600000L

        if (stepTime == 0L) {
            time
        }

        val decimal = time % stepTime
        val necessaryValue = stepTime - decimal

        println(decimal)
        println(necessaryValue)
        if (decimal == 0L) {
            time
        } else {
            if (decimal > stepTime / 2) {
                time + necessaryValue
            } else {
                time - decimal
            }
        }
    }

    @Test
    fun addVideoTest() {
        val url1 = "https://youtu.be/4jMoeE9J7aQ?si=h9JGV84cTNXmCfp9"
        val url2 =
            "https://www.youtube.com/watch?v=_O0sp4C0gM0&ab_channel=%EA%B9%80%EC%9E%AC%EC%9B%90%EC%9D%98%EC%A6%90%EA%B1%B0%EC%9A%B4%EC%84%B8%EC%83%81"
        val errorUrl = "https://naver.com"
        runBlocking {
            val builder = Retrofit.Builder()
                .baseUrl(Constants.YOUTUBE_API_BASE_URL)
                .client(OkHttpClient.Builder().apply {
                    readTimeout(2, TimeUnit.MINUTES)
                }.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            val retrofit = builder.create(RetrofitService::class.java)

            val id1 = url1.substringAfter("https://youtu.be/")
                .substringBefore("?")
            val id2 = url2.substringAfter("https://www.youtube.com/watch?v=")
                .substringBefore("&")
            val id3 = url2.substringAfter("https://youtu.be/")
                .substringBefore("?")

            println(id1)
            println(id2)
            println(id3)
            println(errorUrl.substringAfter("https://").substringBefore("/"))
//            println(result1)
//            println(result2)
        }
    }

    @Test
    fun translateTest() {
        val userEntity = UserEntity(
            userId = "abc",
            name = "Nickname",
            color = Blue.toArgb()
        )
//        val test = Json.encodeToString(UserEntity.serializer(), userEntity)
//        val byteArr = test.toByteArray()
//
//        println(byteArr)
//        println(byteArr.toString(Charset.defaultCharset()))
//        println(
//            Json.decodeFromString(
//                UserEntity.serializer(),
//                byteArr.toString(Charset.defaultCharset())
//            )
//        )
        val payloadType = PayloadType.ClientJoinTimer(userEntity)
        val json = Json.encodeToString(PayloadType.serializer(), payloadType)
        val type = Json.decodeFromString(PayloadType.serializer(), json)

        println(json)
        println(type)
    }
}