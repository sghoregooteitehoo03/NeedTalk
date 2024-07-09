package com.sghore.needtalk.presentation.ui.talkhistory_detail_screen

sealed interface TalkHistoryDetailUiEvent {
    data object ClickNavigateUp : TalkHistoryDetailUiEvent

    data object OptionInfo : TalkHistoryDetailUiEvent

    data object OptionRenameTitle : TalkHistoryDetailUiEvent

    data object OptionRemoveTalkHistory : TalkHistoryDetailUiEvent

    data class ChangeTime(val time: Long) : TalkHistoryDetailUiEvent

    data class ClickPlayOrPause(val isPlay: Boolean) : TalkHistoryDetailUiEvent

    data object ClickBeforeSecond : TalkHistoryDetailUiEvent

    data object ClickAfterSecond : TalkHistoryDetailUiEvent

    data object ClickClips : TalkHistoryDetailUiEvent

    data object ClickMakeClip : TalkHistoryDetailUiEvent
}