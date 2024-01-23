package com.sghore.needtalk.util

import java.text.DecimalFormat

fun parseMinuteSecond(timeStamp: Long): String {
    val decimalFormat = DecimalFormat("#00")

    val minute = (timeStamp / 60000L).toInt()
    val second = (timeStamp % 60000L).toInt() / 1000

    return "${decimalFormat.format(minute)}:${decimalFormat.format(second)}"
}