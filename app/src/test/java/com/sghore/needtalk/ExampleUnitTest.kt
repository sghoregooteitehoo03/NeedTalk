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
        val testArr = listOf("123", "456", "789")
        val joinStr = testArr.joinToString { it }

        println(joinStr)
        joinStr.split(",").map {
            println(it.trim())
        }
    }
}