package com.sghore.needtalk.presentation.ui.create_screen

import com.sghore.needtalk.data.model.entity.MusicEntity

sealed interface CreateUiEvent {
    data object ClickBackArrow : CreateUiEvent
    data class ChangeTime(val talkTime: Long) : CreateUiEvent
    data class ClickStopWatchMode(val isAllow: Boolean) : CreateUiEvent

    data object ClickAddMusic : CreateUiEvent

    data class ClickRemoveMusic(val musicEntity: MusicEntity) : CreateUiEvent

    data class ClickAllowRepeatMusic(val isAllow: Boolean) : CreateUiEvent

    data class ClickNumberOfPeople(val number: Int) : CreateUiEvent

    data object SuccessInsertMusic : CreateUiEvent
    data class FailInsertMusic(val message: String) : CreateUiEvent

    data object SuccessRemoveMusic : CreateUiEvent

}