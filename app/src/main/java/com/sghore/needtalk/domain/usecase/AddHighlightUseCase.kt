package com.sghore.needtalk.domain.usecase

import android.os.Environment
import com.arthenica.ffmpegkit.FFmpegKit
import com.sghore.needtalk.data.model.entity.TalkHighlightEntity
import com.sghore.needtalk.data.repository.TalkRepository
import java.io.File
import javax.inject.Inject

class AddHighlightUseCase @Inject constructor(
    private val talkRepository: TalkRepository
) {
    suspend operator fun invoke(
        recordFilePath: String,
        title: String,
        startTime: Int,
        duration: Int,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val directoryPath = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_MUSIC + "/대화가필요해/highlights"
        )
        val outputPath = File(directoryPath, "${title}|${System.currentTimeMillis()}.m4a")

        if (!directoryPath.exists()) {
            directoryPath.mkdirs()
        }

        val command =
            "-i $recordFilePath -ss ${startTime.div(1000)} -t ${duration.div(1000)} -c copy $outputPath"
        val result = FFmpegKit.execute(command)

        if (result?.returnCode?.isValueSuccess == true) {
            try {
                val talkHighlightEntity = TalkHighlightEntity(filePath = outputPath.path)
                talkRepository.insertTalkHighlightEntity(talkHighlightEntity)

                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
                onError("작업 중 오류가 발생하였습니다.")
            }
        } else {
            onError("작업 중 오류가 발생하였습니다.")
        }
    }
}