package com.sghore.needtalk.presentation.ui

sealed class UiScreen(val route: String) {
    data object HomeScreen : UiScreen(route = "Home")
    data object CreateScreen : UiScreen(route = "Create")
}

sealed class DialogScreen() {
    data object DialogDismiss : DialogScreen()
    data object DialogSetName : DialogScreen()
    data object DialogCreateMusic : DialogScreen()
}