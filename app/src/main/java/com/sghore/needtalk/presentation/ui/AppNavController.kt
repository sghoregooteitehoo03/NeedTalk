package com.sghore.needtalk.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
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
                navigateToTimer = { timerCmInfo ->
                    navigateToHostTimerScreen(
                        navController = navController,
                        userEntity = gViewModel.getUserEntity(),
                        packageName = context.packageName,
                        timerCmInfo = timerCmInfo
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
                        hostEndpointId = timerInfo.hostEndpointId,
                        packageName = context.packageName
                    )
                },
                showSnackBar = showSnackBar
            )
        }
        composable(
            route = UiScreen.HostTimerScreen.route +
                    "?userEntity={userEntity}&timerCmInfo={timerCmInfo}&packageName={packageName}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType },
                navArgument("timerCmInfo") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            HostTimerRoute(
                navigateUp = {
                    navigateToHome(navController)
                }
            )
        }
        composable(
            route = UiScreen.ClientTimerScreen.route +
                    "?userEntity={userEntity}&hostEndpointId={hostEndpointId}&packageName={packageName}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType },
                navArgument("hostEndpointId") { type = NavType.StringType },
                navArgument("packageName") { type = NavType.StringType }
            )
        ) {
            ClientTimerRoute(
                navigateUp = {
                    navigateToHome(navController)
                }
            )
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
    timerCmInfo: TimerCommunicateInfo
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)
        val timerCmInfoJson = Json.encodeToString(TimerCommunicateInfo.serializer(), timerCmInfo)
            .replace("&", "%26")

        navController.navigate(
            UiScreen.HostTimerScreen.route +
                    "?&userEntity=${userEntityJson}&timerCmInfo=${timerCmInfoJson}&packageName=${packageName}"
        )
    }
}

private fun navigateToClientTimerScreen(
    navController: NavHostController,
    userEntity: UserEntity?,
    hostEndpointId: String,
    packageName: String
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)

        navController.navigate(
            UiScreen.ClientTimerScreen.route +
                    "?&userEntity=${userEntityJson}&hostEndpointId=${hostEndpointId}&packageName=${packageName}"
        )
    }
}

private fun navigateToHome(navController: NavHostController) {
    navController.popBackStack(route = UiScreen.HomeScreen.route, inclusive = false)
}