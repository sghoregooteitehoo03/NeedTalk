package com.sghore.needtalk.util

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.sghore.needtalk.presentation.ui.theme.Blue
import com.sghore.needtalk.presentation.ui.theme.Green
import com.sghore.needtalk.presentation.ui.theme.Orange50
import com.sghore.needtalk.presentation.ui.theme.Red
import com.sghore.needtalk.presentation.ui.theme.Sky
import java.text.DecimalFormat
import java.util.Calendar

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

fun getRandomColor(index: Int): Color {
    // Function to generate distinct colors based on index
    val colors = listOf(Orange50, Color(0xFF936F26), Color(0xFF5C3D00))
    return colors[index % colors.size]
}

fun getFirstTime(time: Long): Long {
    val calendar = Calendar.getInstance().apply { this.timeInMillis = time }

    return calendar.apply {
        set(Calendar.DAY_OF_WEEK, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}

fun getLastTime(time: Long): Long {
    val calendar = Calendar.getInstance().apply { this.timeInMillis = time }

    return calendar.apply {
        set(Calendar.DAY_OF_WEEK, 7)
        set(Calendar.HOUR_OF_DAY, 23)
        set(Calendar.MINUTE, 59)
        set(Calendar.SECOND, 59)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis
}