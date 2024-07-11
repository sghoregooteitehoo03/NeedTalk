package com.sghore.needtalk.component

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import com.sghore.needtalk.util.Constants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.log10
import kotlin.math.sqrt

class AudioRecorder {
    private val sampleRate = Constants.AUDIO_SAMPLE_RATE // 44.1kHz
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    private var recordJob: Job? = null

    private var isRecording: Boolean = false// 녹음 되고 있는지
    private var isPaused = false // 멈춤 여부
    private var audioRecord: AudioRecord? = null


    // 녹음 시작
    @SuppressLint("MissingPermission")
    fun startRecording(
        outputFile: File,
        amplitudeFlow: MutableStateFlow<Int>,
        scope: CoroutineScope
    ) {
        if (!isRecording) {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                sampleRate,
                channelConfig,
                audioFormat,
                bufferSize
            )
            isRecording = true
            isPaused = false

            recordJob?.cancel()
            recordJob = scope.launch(Dispatchers.IO) { // 녹음 데이터 Write
                writeAudioDataToFile(outputFile, amplitudeFlow)
            }
        }
    }

    // 녹음 재개
    fun resumeRecording() {
        if (isRecording && isPaused) {
            isPaused = false
        }
    }

    // 녹음 정지
    fun pauseRecording() {
        if (isRecording) {
            isPaused = true
        }
    }

    // 녹음 종료
    fun stopRecording() {
        isRecording = false
        isPaused = false

        recordJob?.cancel()
        recordJob = null

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    // 녹음 데이터를 지정된 경로에 입력
    private suspend fun writeAudioDataToFile(
        outputFile: File,
        amplitudeFlow: MutableStateFlow<Int>
    ) {
        val buffer = ByteArray(bufferSize)
        val outputStream = FileOutputStream(outputFile)
        val bufferedOutputStream = BufferedOutputStream(outputStream)
        var bytesRead: Int

        try {
            audioRecord?.startRecording() // 녹음
            while (isRecording) {
                if (!isPaused) { // 정지 되지 않았다면
                    bytesRead = audioRecord!!.read(buffer, 0, bufferSize) // 녹음 데이터 읽기
                    if (bytesRead != AudioRecord.ERROR_INVALID_OPERATION && bytesRead != AudioRecord.ERROR_BAD_VALUE) {
                        bufferedOutputStream.write(buffer, 0, bytesRead) // 녹음 데이터 쓰기

                        Log.i("Check", "BytesRead: $bytesRead")
                        val amplitude = amplitudeToDecibel(buffer)
                        amplitudeFlow.update { amplitude.toInt() } // 데시벨 전달
                    }
                } else {
                    delay(100)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                bufferedOutputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // 파형 계산
    private fun amplitudeToDecibel(audioBuffer: ByteArray): Double {
        var sum = 0.0

        for (i in audioBuffer.indices step 2) {
            val sample =
                ((audioBuffer[i + 1].toInt() shl 8) or (audioBuffer[i].toInt() and 0xff)).toShort()
            sum += sample.toDouble() * sample.toDouble()
        }

        val amplitude = sqrt(sum / (audioBuffer.size / 2))
        val amplitudeDb = 20 * log10(amplitude)
        return amplitudeDb
    }
}