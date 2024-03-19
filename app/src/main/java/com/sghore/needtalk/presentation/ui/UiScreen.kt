package com.sghore.needtalk.presentation.ui

sealed class UiScreen(val route: String) {
    data object HomeScreen : UiScreen(route = "Home")
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