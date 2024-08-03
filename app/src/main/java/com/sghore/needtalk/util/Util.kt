package com.sghore.needtalk.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaRecorder
import android.os.Build
import com.sghore.needtalk.domain.model.TalkTopicCategory
import java.io.ByteArrayOutputStream
import java.io.File
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.Locale

fun getDefaultTalkTitle(): String = SimpleDateFormat(
    "yy-MM-dd HH:mm 대화",
    Locale.KOREA
).format(System.currentTimeMillis())

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

// 대화시간에 따라 랜덤으로 친밀도를 반환
fun getRandomExperiencePoint(talkTime: Long): Double {
    val maxDivideTime = 7200000L // 2시간
    val minDivideTime = 300000L // 5분
    val maxDivideValue = 24 // 5분단위로 2시간 기준
    val maxExperiencePoints = (32..35).toList().random()
    val urgentPoint = maxExperiencePoints.toFloat() / maxDivideValue
    var divideValue = 0

    for (i in 1..maxDivideValue) {
        if (talkTime < minDivideTime * i) {
            break
        }

        divideValue = i
    }

    return if (talkTime > maxDivideTime) {
        val value = urgentPoint * divideValue + getRandomExperiencePoint(talkTime - maxDivideTime)
        kotlin.math.round(value * 10) / 10.0
    } else {
        val value = urgentPoint * divideValue
        kotlin.math.round(value * 10) / 10.0
    }
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

fun getMediaRecord(context: Context, setOutputFileName: (String) -> Unit) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { // 미디어 레코드 설정
        MediaRecorder(context).apply {
            // 경로 설정
            val tempDir = context.getExternalFilesDir("recordings")

            if (tempDir?.exists() == false) {
                tempDir.mkdirs()
            }
            val outputFilePath =
                File(tempDir, "record_${System.currentTimeMillis()}.m4a").absolutePath

            // 미디어 옵션 설정
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(outputFilePath)

            setOutputFileName(outputFilePath)
        }
    } else {
        MediaRecorder().apply {
            // 경로 설정
            val tempDir = context.getExternalFilesDir("recordings")

            if (tempDir?.exists() == false) {
                tempDir.mkdirs()
            }
            val outputFilePath =
                File(tempDir, "record_${System.currentTimeMillis()}.m4a").absolutePath

            // 미디어 옵션 설정
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setAudioEncodingBitRate(128000)
            setAudioSamplingRate(44100)
            setOutputFile(outputFilePath)

            setOutputFileName(outputFilePath)
        }
    }

// 파일 사이즈를 형식에 맞게 String으로 변환
fun getFileSizeToStr(fileSize: Long): String {
    val df = DecimalFormat("0.00")

    val sizeKb = 1024.0f
    val sizeMb = sizeKb * sizeKb
    val sizeGb = sizeMb * sizeKb

    return if (fileSize < sizeMb) df.format(fileSize / sizeKb) + "KB"
    else if (fileSize < sizeGb) df.format(fileSize / sizeMb) + " MB"
    else ""
}