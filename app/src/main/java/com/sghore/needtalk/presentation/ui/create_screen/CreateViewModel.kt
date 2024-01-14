package com.sghore.needtalk.presentation.ui.create_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.MusicEntity
import com.sghore.needtalk.domain.usecase.AddYoutubeMusicUseCase
import com.sghore.needtalk.domain.usecase.GetTimerSettingUseCase
import com.sghore.needtalk.domain.usecase.RemoveYoutubeMusicUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateViewModel @Inject constructor(
    private val getTimerSettingUseCase: GetTimerSettingUseCase,
    private val addYoutubeMusicUseCase: AddYoutubeMusicUseCase,
    private val removeYoutubeMusicUseCase: RemoveYoutubeMusicUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CreateUiState())
    private val _uiEvent = MutableSharedFlow<CreateUiEvent>()

    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        CreateUiState()
    )
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    init {
        initState()
    }

    private fun initState() = viewModelScope.launch {
        getTimerSettingUseCase(
            transform = { timerSettingEntity, musicEntities ->
                val defaultMusics = listOf(
                    MusicEntity(
                        "",
                        thumbnailImage = "",
                        "음악 없음",
                        timestamp = 0L
                    )
                )

                if (timerSettingEntity != null) {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            talkTime = timerSettingEntity.talkTime,
                            isStopwatch = timerSettingEntity.isStopwatch,
                            musics = defaultMusics + musicEntities,
                            initialMusicId = timerSettingEntity.selectMusicId,
                            allowRepeatMusic = timerSettingEntity.allowRepeatMusic,
                            numberOfPeople = timerSettingEntity.numberOfPeople
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            musics = defaultMusics + musicEntities
                        )
                    }
                }
            }
        ).collect()
    }

    // 시간 변경 이벤트
    fun changeTalkTime(talkTime: Long) {
        _uiState.update {
            it.copy(talkTime = talkTime)
        }
    }

    // 스톱워치 모드 온 오프
    fun stopwatchOnOff(isAllow: Boolean) {
        _uiState.update {
            it.copy(isStopwatch = isAllow)
        }
    }

    // 음악 반복 온 오프
    fun repeatMusicOnOff(isAllow: Boolean) {
        _uiState.update {
            it.copy(allowRepeatMusic = isAllow)
        }
    }

    // 인원 수 변경
    fun changeNumberOfPeople(number: Int) {
        _uiState.update {
            it.copy(numberOfPeople = number)
        }
    }

    // 보여줄 다이얼로그 화면을 설정
    fun setDialogScreen(dialogScreen: DialogScreen) {
        _uiState.update {
            it.copy(dialogScreen = dialogScreen)
        }
    }

    // 음악 추가
    fun addYoutubeMusic(url: String, title: String) = viewModelScope.launch {
        val id = separateId(url)

        try {
            if (id.isNotEmpty() && title.isNotEmpty()) { // 아이디에 문제가 없다면
                _uiState.update { it.copy(isLoading = true) }

                addYoutubeMusicUseCase(videoId = id, title = title)
                handelEvent(CreateUiEvent.SuccessInsertMusic)
            } else {
                handelEvent(CreateUiEvent.FailInsertMusic("유효한 유튜브 URL을 입력해주세요."))
            }
        } catch (e: Exception) {
            handelEvent(CreateUiEvent.FailInsertMusic("음악 추가간의 오류가 발생하였습니다."))
        }

        _uiState.update { it.copy(isLoading = false) }
    }

    // 음악 삭제
    fun removeMusic(id: String) = viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        removeYoutubeMusicUseCase(id)
        handelEvent(CreateUiEvent.SuccessRemoveMusic)
        _uiState.update { it.copy(isLoading = false) }
    }

    fun handelEvent(event: CreateUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    // Youtube URL에서 영상의 ID를 분리 함
    private fun separateId(url: String): String {
        val baseUrl = url.substringAfter("https://").substringBefore("/")

        return when (baseUrl) {
            Constants.YOUTUBE_BASE_URL_CASE1 -> {
                url.substringAfter("https://youtu.be/")
                    .substringBefore("?")
            }

            Constants.YOUTUBE_BASE_URL_CASE2 -> {
                url.substringAfter("https://www.youtube.com/watch?v=")
                    .substringBefore("&")
            }

            Constants.YOUTUBE_BASE_URL_CASE3 -> {
                url.substringAfter("https://m.youtube.com/watch?v=")
                    .substringBefore("&")
            }

            else -> ""
        }
    }
}