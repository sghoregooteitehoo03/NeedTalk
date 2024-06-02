package com.sghore.needtalk.presentation.ui

import com.sghore.needtalk.R

sealed class UiScreen(
    val route: String,
    val bottomName: String = "",
    val bottomIcon: Int = -1,
) {
    data object Nothing : UiScreen(route = "")

    data object EmptyScreen : UiScreen(route = "EmptyScreen")

    data object PermissionScreen : UiScreen(route = "PermissionScreen")

    data object StartScreen : UiScreen(route = "StartScreen")

    data object CreateProfileScreen : UiScreen(route = "CreateProfileScreen")

    data object HomeScreen : UiScreen(route = "Home")

    data object TalkHistoryScreen : UiScreen(
        route = "TalkHistoryScreen",
        bottomName = "대화 기록",
        bottomIcon = R.drawable.ic_talk_history
    )

    data object TalkTopicsScreen : UiScreen(
        route = "TalkTopicsScreen",
        bottomName = "대화 주제",
        bottomIcon = R.drawable.ic_talk_topic
    )

    data object CreateScreen : UiScreen(route = "Create")

    data object HostTimerScreen : UiScreen(route = "HostTimer")

    data object ClientTimerScreen : UiScreen(route = "ClientTimer")

    data object JoinScreen : UiScreen(route = "Join")

    data object StaticsScreen : UiScreen(route = "StaticsScreen")
}

sealed interface DialogScreen {
    data object DialogDismiss : DialogScreen
    data object DialogSetName : DialogScreen

    data class DialogTalkTopics(val topicCategory: String, val groupCode: Int) : DialogScreen

    data object DialogAddTopic : DialogScreen

    data class DialogWarning(
        val message: String,
        val isError: Boolean = false,
        val isReject: Boolean = false
    ) : DialogScreen

    data object DialogTimerReady : DialogScreen
}