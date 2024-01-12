package com.sghore.needtalk.presentation.ui.create_screen

import com.sghore.needtalk.data.model.MusicEntity

data class CreateUiState(
    val talkTime: Long = 600000L,
    val isStopwatch: Boolean = false,
    val musics: List<MusicEntity> = listOf(
        MusicEntity(
            "",
            thumbnailImage = "",
            "음악 없음",
            timestamp = 0L
        )
    ),
    val initialMusicId: String = "",
    val allowRepeatMusic: Boolean = true,
    val numberOfPeople: Int = 2
)
