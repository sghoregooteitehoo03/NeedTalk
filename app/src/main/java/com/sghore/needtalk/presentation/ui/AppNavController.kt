package com.sghore.needtalk.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.presentation.ui.create_screen.CreateRoute
import com.sghore.needtalk.presentation.ui.home_screen.HomeRoute
import com.sghore.needtalk.presentation.ui.join_screen.JoinRoute
import com.sghore.needtalk.presentation.ui.host_timer_screen.TimerRoute
import kotlinx.serialization.json.Json

@Composable
fun AppNavHost(
    gViewModel: GlobalViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = UiScreen.HomeScreen.route
    ) {
        composable(UiScreen.HomeScreen.route) {
            HomeRoute(
                navigateToCreateScreen = {
                    navigateToCreateScreen(navController, gViewModel.getUserEntity())
                },
                navigateToJoinScreen = {
                    navigateToJoinScreen(
                        navController,
                        gViewModel.getUserEntity(),
                        context.packageName
                    )
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
                    navigateToHostTimerScreen(
                        navController = navController,
                        userEntity = gViewModel.getUserEntity(),
                        packageName = context.packageName,
                        timerSettingEntity = timerSettingEntity
                    )
                }
            )
        }
        composable(
            route = UiScreen.JoinScreen.route +
                    "?userEntity={userEntity}&packageName={packageName}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            JoinRoute(navigateUp = navController::navigateUp)
        }
        composable(
            route = UiScreen.HostTimerScreen.route +
                    "?userEntity={userEntity}&timerSetting={timerSetting}&packageName={packageName}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType },
                navArgument("timerSetting") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
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

private fun navigateToJoinScreen(
    navController: NavHostController,
    userEntity: UserEntity?,
    packageName: String
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)
        navController.navigate(
            UiScreen.JoinScreen.route +
                    "?&userEntity=${userEntityJson}&packageName=${packageName}"
        )
    }
}

private fun navigateToHostTimerScreen(
    navController: NavHostController,
    userEntity: UserEntity?,
    packageName: String,
    timerSettingEntity: TimerSettingEntity
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)
        val timerSettingJson =
            Json.encodeToString(TimerSettingEntity.serializer(), timerSettingEntity)

        navController.navigate(
            UiScreen.HostTimerScreen.route +
                    "?&userEntity=${userEntityJson}&timerSetting=${timerSettingJson}&packageName=${packageName}"
        )
    }
}