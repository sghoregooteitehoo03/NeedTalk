package com.sghore.needtalk.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.presentation.ui.create_screen.CreateRoute
import com.sghore.needtalk.presentation.ui.home_screen.HomeRoute
import com.sghore.needtalk.presentation.ui.timer_screen.TimerRoute
import kotlinx.serialization.json.Json

@Composable
fun AppNavHost(
    gViewModel: GlobalViewModel,
    navController: NavHostController
) {
    NavHost(
        navController = navController,
        startDestination = UiScreen.HomeScreen.route
    ) {
        composable(UiScreen.HomeScreen.route) {
            HomeRoute(
                navigateToCreateScreen = {
                    navigateToCreateScreen(navController, gViewModel.getUserEntity())
                },
                updateUserEntity = { userEntity -> gViewModel.setUserEntity(userEntity) }
            )
        }
        composable(
            route = UiScreen.CreateScreen.route + "?userEntity={userEntity}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType }
            )
        ) {
            CreateRoute(
                navigateUp = navController::navigateUp,
                navigateToTimer = { timerSettingEntity ->
                    navigateToTimer(navController, timerSettingEntity)
                }
            )
        }
        composable(route = UiScreen.TimerScreen.route) {
            TimerRoute()
        }
    }
}

private fun navigateToCreateScreen(
    navController: NavHostController,
    userEntity: UserEntity?
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)
        navController.navigate(UiScreen.CreateScreen.route + "?userEntity=${userEntityJson}")
    }
}

private fun navigateToTimer(
    navController: NavHostController,
    timerSettingEntity: TimerSettingEntity
) {
    navController.navigate(UiScreen.TimerScreen.route)
}