package com.sghore.needtalk

import org.junit.Test

import org.junit.Assert.*

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
    fun function_test() {
        println(getTimerTimeByStep(256000, 600000))
    }

    private fun getTimerTimeByStep(time: Long, stepTime: Long): Long {
        if (stepTime == 0L) {
            return time
        }

        val decimal = time % stepTime
        val necessaryValue = stepTime - decimal

        println(decimal)
        println(necessaryValue)
        return if (decimal == 0L) {
            time
        } else {
            if (decimal > stepTime / 2) {
                time + necessaryValue
            } else {
                time - decimal
            }
        }
    }
}