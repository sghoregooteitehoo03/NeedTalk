package com.sghore.needtalk.presentation.ui

sealed class UiScreen(val route: String) {
    data object HomeScreen : UiScreen(route = "Home")
}