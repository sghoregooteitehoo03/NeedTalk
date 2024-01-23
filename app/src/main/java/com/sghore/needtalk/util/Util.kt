package com.sghore.needtalk.util

import java.text.DecimalFormat

fun parseMinuteSecond(timeStamp: Long): String {
    return if (timeStamp > 0L) {
        val decimalFormat = DecimalFormat("#00")

        val minute = (timeStamp / 60000L).toInt()
        val second = (timeStamp % 60000L).toInt() / 1000

        "${decimalFormat.format(minute)}:${decimalFormat.format(second)}"
    } else {
        "∞"
    }
}