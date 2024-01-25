package com.sghore.needtalk.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import java.text.DecimalFormat

fun parseMinuteSecond(timeStamp: Long): String {
    return if (timeStamp >= 0L) {
        val decimalFormat = DecimalFormat("#00")

        val minute = (timeStamp / 60000L).toInt()
        val second = (timeStamp % 60000L).toInt() / 1000

        "${decimalFormat.format(minute)}:${decimalFormat.format(second)}"
    } else {
        "∞"
    }
}

fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
    val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

    Palette.from(bmp).generate { palette ->
        palette?.dominantSwatch?.rgb?.let { colorValue ->
            onFinish(Color(colorValue))
        }
    }
}
