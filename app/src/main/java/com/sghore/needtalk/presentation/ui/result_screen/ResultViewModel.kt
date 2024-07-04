package com.sghore.needtalk.presentation.ui.result_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.model.UserTalkResult
import com.sghore.needtalk.domain.usecase.GetUserDataUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.io.File
import java.text.DecimalFormat
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
@HiltViewModel
class ResultViewModel @Inject constructor(
    private val getUserDataUseCase: GetUserDataUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // UI State
    private val _uiState = MutableStateFlow(ResultUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = ResultUiState()
    )

    init {
        val filePath = savedStateHandle.get<String>("filePath")
        val userTalkResultJsonArr =
            savedStateHandle.get<String>("userTalkResults") ?: ""

        if (filePath != null && userTalkResultJsonArr.isNotEmpty()) {
            // 원본으로 변환
            val userTalkResults: List<UserTalkResult> = Json.decodeFromString(userTalkResultJsonArr)
            initUiState(filePath, userTalkResults)
        }
    }

    // UI State 초기화
    private fun initUiState(path: String, userTalkResults: List<UserTalkResult>) =
        viewModelScope.launch {
            val fileSize = if(path.isEmpty()) {
                ""
            } else {
                getFileSize(path)
            }
            val otherUsers = userTalkResults.map {
                getUserDataUseCase(it.userId)
            }

            _uiState.update {
                it.copy(
                    fileSize = fileSize,
                    otherUsers = otherUsers,
                    userTalkResult = userTalkResults,
                    isLoading = false
                )
            }
        }

    private fun getFileSize(filePath: String): String {
        val size = File(filePath).length()
        val df = DecimalFormat("0.00")

        val sizeKb = 1024.0f
        val sizeMb = sizeKb * sizeKb
        val sizeGb = sizeMb * sizeKb

        return if (size < sizeMb) df.format(size / sizeKb) + "KB"
        else if (size < sizeGb) df.format(size / sizeMb) + " MB"
        else ""
    }
}