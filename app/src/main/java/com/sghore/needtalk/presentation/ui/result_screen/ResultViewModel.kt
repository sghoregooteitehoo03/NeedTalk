package com.sghore.needtalk.presentation.ui.result_screen

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.sghore.needtalk.domain.model.UserTalkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
@HiltViewModel
class ResultViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    init {
        val filePath = savedStateHandle.get<String>("filePath")
        val userTalkResultJsonArr =
            savedStateHandle.get<String>("userTalkResults") ?: ""

        if (filePath != null && userTalkResultJsonArr.isNotEmpty()) {
            // 원본으로 변환
            val userTalkResults: List<UserTalkResult> = Json.decodeFromString(userTalkResultJsonArr)

            Log.i("CheckData", "path: ${filePath}, results: ${userTalkResults}")
        }
    }
}