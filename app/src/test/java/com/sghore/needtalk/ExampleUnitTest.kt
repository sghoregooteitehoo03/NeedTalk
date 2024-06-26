package com.sghore.needtalk

import androidx.compose.ui.graphics.toArgb
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.PayloadType
import com.sghore.needtalk.presentation.ui.theme.Blue
import kotlinx.serialization.json.Json
import org.junit.Test
import org.junit.Assert.*
import java.util.Calendar

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
//        val payloadType = PayloadType.ClientJoinTimer(userEntity)
//        val json = Json.encodeToString(PayloadType.serializer(), payloadType)
//        val type = Json.decodeFromString(PayloadType.serializer(), json)
//
//        println(json)
//        println(type)
    }

    @Test
    fun getWeekTest() {
        val testDate = System.currentTimeMillis()
        val calendar = Calendar.getInstance().apply { this.timeInMillis = testDate }
        val firstTime = calendar.apply {
            set(Calendar.DAY_OF_WEEK, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val lastTime = calendar.apply {
            set(Calendar.DAY_OF_WEEK, 7)
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        println("first: ${firstTime}, last: $lastTime")
    }

    @Test
    fun test() {
        val testList = listOf("123", "456")
        val list = testList.map {
            it.toInt()
        }
        println(list)
    }
}