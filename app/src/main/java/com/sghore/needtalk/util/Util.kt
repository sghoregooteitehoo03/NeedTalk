package com.sghore.needtalk.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.palette.graphics.Palette
import com.sghore.needtalk.domain.model.TalkTopicCategory
import com.sghore.needtalk.presentation.ui.theme.Orange50
import java.io.ByteArrayOutputStream
import java.security.MessageDigest
import java.text.DecimalFormat
import java.util.Calendar

// time이 step값에 따라 값이 증가할 수 있도록 값을 보강 및 감소해주는 역할을 수행하는 함수
fun getTimerTimeByStep(time: Long, stepTime: Long): Long {
    if (stepTime == 0L) {
        return time
    }

    val decimal = time % stepTime
    val necessaryValue = stepTime - decimal

    return if (decimal == 0L) {
        time
    } else {
        if (decimal > stepTime / 2000) {
            time + necessaryValue
        } else {
            time - decimal
        }
    }
}

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

// 카테고리코드 -> 카테고리 변환
fun getCodeToCategory(code: Int) = when (code) {
    TalkTopicCategory.Friend.code -> TalkTopicCategory.Friend
    TalkTopicCategory.Couple.code -> TalkTopicCategory.Couple
    TalkTopicCategory.Family.code -> TalkTopicCategory.Family
    TalkTopicCategory.Balance.code -> TalkTopicCategory.Balance
    TalkTopicCategory.SmallTalk.code -> TalkTopicCategory.SmallTalk
    TalkTopicCategory.DeepTalk.code -> TalkTopicCategory.DeepTalk
    else -> null
}

// 이미지 인코딩/디코딩 과정
fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
    return outputStream.toByteArray()
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}

// 이미지 리소스를 Bitmap으로 변환
fun getBitmapFromResource(context: Context, drawableId: Int): Bitmap {
    val options = BitmapFactory.Options()
    options.inMutable = true

    return BitmapFactory.decodeResource(context.resources, drawableId, options)
}

// 이미지 병합
fun mergeImages(bitmaps: List<Bitmap>): Bitmap {
    val width = bitmaps.maxOf { it.width }
    val height = bitmaps.maxOf { it.height }

    // 새로운 비트맵을 만듭니다.
    val result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(result)
    val paint = Paint()

    for (bitmap in bitmaps) {
        // 현재 비트맵을 그릴 위치 계산
        val left = 0
        val top = (height - bitmap.height) / 2 // 모든 비트맵의 높이가 같다고 가정합니다.

        // 비트맵을 캔버스에 그립니다.
        canvas.drawBitmap(bitmap, left.toFloat(), top.toFloat(), paint)
    }

    return result
}

fun generateTalkTopicId(userId: String, currentTime: Long): String {
    val input = userId + currentTime
    val bytes = MessageDigest
        .getInstance("SHA-256")
        .digest(input.toByteArray())

    return bytes.joinToString("") { "%02x".format(it) }
}