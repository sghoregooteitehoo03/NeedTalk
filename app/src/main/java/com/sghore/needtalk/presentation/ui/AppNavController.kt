package com.sghore.needtalk.presentation.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sghore.needtalk.presentation.ui.create_screen.CreateRoute
import com.sghore.needtalk.presentation.ui.home_screen.HomeRoute

@Composable
fun AppNavHost(
    navController: NavHostController,
    startDestination: String = UiScreen.HomeScreen.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(UiScreen.HomeScreen.route) {
            HomeRoute(
                navigateToCreateScreen = { navigateToCreateScreen(navController) }
            )
        }
        composable(UiScreen.CreateScreen.route) {
            CreateRoute()
        }
    }
}

private fun navigateToCreateScreen(navController: NavHostController) {
    navController.navigate(UiScreen.CreateScreen.route)
}