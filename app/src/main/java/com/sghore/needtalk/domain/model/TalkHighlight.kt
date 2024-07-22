package com.sghore.needtalk.domain.model

import java.io.File

data class TalkHighlight(
    val id: Int?,
    val title: String,
    val file: File,
    val duration: Int,
)