package com.sghore.needtalk.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TimerInfo
import com.sghore.needtalk.presentation.ui.create_screen.CreateRoute
import com.sghore.needtalk.presentation.ui.home_screen.HomeRoute
import com.sghore.needtalk.presentation.ui.join_screen.JoinRoute
import com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen.ClientTimerRoute
import com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen.HostTimerRoute
import kotlinx.serialization.json.Json

@Composable
fun AppNavHost(
    modifier: Modifier,
    gViewModel: GlobalViewModel,
    navController: NavHostController,
    showSnackBar: suspend (String) -> Unit
) {
    val context = LocalContext.current

    NavHost(
        modifier = modifier,
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
                navigateToTimer = { timerInfo ->
                    navigateToHostTimerScreen(
                        navController = navController,
                        userEntity = gViewModel.getUserEntity(),
                        packageName = context.packageName,
                        timerInfo = timerInfo
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
            JoinRoute(
                navigateUp = navController::navigateUp,
                navigateToTimerScreen = { timerInfo ->
                    navigateToClientTimerScreen(
                        navController = navController,
                        userEntity = gViewModel.getUserEntity(),
                        packageName = context.packageName,
                        timerInfo = timerInfo
                    )
                },
                showSnackBar = showSnackBar
            )
        }
        composable(
            route = UiScreen.HostTimerScreen.route +
                    "?userEntity={userEntity}&timerInfo={timerInfo}&packageName={packageName}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType },
                navArgument("timerInfo") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            HostTimerRoute()
        }
        composable(
            route = UiScreen.ClientTimerScreen.route +
                    "?userEntity={userEntity}&timerInfo={timerInfo}&packageName={packageName}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType },
                navArgument("timerInfo") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            ClientTimerRoute()
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
    timerInfo: TimerInfo
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)
        val timerInfoJson = Json.encodeToString(TimerInfo.serializer(), timerInfo)
            .replace("&", "%26")

        navController.navigate(
            UiScreen.HostTimerScreen.route +
                    "?&userEntity=${userEntityJson}&timerInfo=${timerInfoJson}&packageName=${packageName}"
        )
    }
}

private fun navigateToClientTimerScreen(
    navController: NavHostController,
    userEntity: UserEntity?,
    packageName: String,
    timerInfo: TimerInfo
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)
        val timerInfoJson = Json.encodeToString(TimerInfo.serializer(), timerInfo)
            .replace("&", "%26")

        navController.navigate(
            UiScreen.ClientTimerScreen.route +
                    "?&userEntity=${userEntityJson}&timerInfo=${timerInfoJson}&packageName=${packageName}"
        )
    }
}