package com.sghore.needtalk.presentation.ui

import com.sghore.needtalk.data.model.entity.MusicEntity

sealed class UiScreen(val route: String) {
    data object HomeScreen : UiScreen(route = "Home")
    data object CreateScreen : UiScreen(route = "Create")
}

sealed interface DialogScreen {
    data object DialogDismiss : DialogScreen
    data object DialogSetName : DialogScreen
    data object DialogCreateMusic : DialogScreen

    data class DialogRemoveMusic(val musicEntity: MusicEntity) : DialogScreen
}