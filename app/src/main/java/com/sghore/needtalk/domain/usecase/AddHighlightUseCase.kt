package com.sghore.needtalk.domain.usecase

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
        directoryPath: String,
        title: String,
        startTime: Int,
        duration: Int,
        talkHistoryId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val outputPath = File(directoryPath, "${title}|${System.currentTimeMillis()}.m4a")

        val command =
            "-i $recordFilePath -ss ${startTime.div(1000)} -t ${duration.div(1000)} -c copy $outputPath"
        val result = FFmpegKit.execute(command)

        if (result?.returnCode?.isValueSuccess == true) {
            try {
                val talkHighlightEntity = TalkHighlightEntity(
                    title = title,
                    filePath = outputPath.path,
                    duration = duration,
                    talkHistoryId = talkHistoryId
                )
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