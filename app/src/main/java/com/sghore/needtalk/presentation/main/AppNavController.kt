package com.sghore.needtalk.presentation.main

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.presentation.ui.UiScreen
import com.sghore.needtalk.presentation.ui.add_talktopic_screen.AddTalkTopicRoute
import com.sghore.needtalk.presentation.ui.create_profile_screen.CreateProfileRoute
import com.sghore.needtalk.presentation.ui.create_screen.CreateRoute
import com.sghore.needtalk.presentation.ui.empty_screen.EmptyRoute
import com.sghore.needtalk.presentation.ui.home_screen.HomeScreen
import com.sghore.needtalk.presentation.ui.join_screen.JoinRoute
import com.sghore.needtalk.presentation.ui.permission_screen.PermissionRoute
import com.sghore.needtalk.presentation.ui.start_screen.StartRoute
import com.sghore.needtalk.presentation.ui.statics_screen.StaticsRoute
import com.sghore.needtalk.presentation.ui.talk_topics_detail_screen.TalkTopicsDetailRoute
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
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = UiScreen.EmptyScreen.route
    ) {
        composable(UiScreen.EmptyScreen.route) {
            EmptyRoute(
                onUpdateUserData = {
                    gViewModel.setUserData(it)
                },
                navigateOtherScreen = { route ->
                    navController.navigate(route) {
                        popUpTo(UiScreen.EmptyScreen.route) { inclusive = true }
                    }
                }
            )
        }
        composable(UiScreen.PermissionScreen.route) {
            PermissionRoute(navigateOtherScreen = {
                navController.navigate(
                    if (gViewModel.getUserData() != null) {
                        UiScreen.HomeScreen.route
                    } else {
                        UiScreen.StartScreen.route
                    }
                ) {
                    popUpTo(UiScreen.EmptyScreen.route) { inclusive = true }
                }
            })
        }

        composable(UiScreen.StartScreen.route) {
            StartRoute(navigateToCreateProfile = {
                navController.navigate(UiScreen.CreateProfileScreen.route)
            })
        }

        composable(UiScreen.CreateProfileScreen.route) {
            CreateProfileRoute(
                onUpdateUserData = {
                    gViewModel.setUserData(it)
                    navController.navigate(UiScreen.HomeScreen.route) {
                        popUpTo(0) { inclusive = true } // 모든 백스택 제거
                    }
                }
            )
        }

        composable(route = UiScreen.HomeScreen.route) {
            HomeScreen(
                gViewModel = gViewModel,
                navigateToOther = { navController.navigate(route = it) }
            )
        }

        composable(route = UiScreen.AddTalkTopicScreen.route) {
            AddTalkTopicRoute(
                userData = gViewModel.getUserData(),
                navigateBack = { navController.navigateUp() }
            )
        }

        composable(
            route = UiScreen.TalkTopicsDetailScreen.route +
                    "?type={type}",
            arguments = listOf(
                navArgument("type") { type = NavType.StringType },
            )
        ) {
            TalkTopicsDetailRoute()
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
                        hostEndpointId = timerInfo.hostEndpointId
                    )
                },
                showSnackBar = showSnackBar
            )
        }
        composable(
            route = UiScreen.HostTimerScreen.route +
                    "?userEntity={userEntity}&timerCmInfo={timerCmInfo}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType },
                navArgument("timerCmInfo") { type = NavType.StringType }
            )
        ) {
            HostTimerRoute(
                navigateUp = {
                    gViewModel.setIsRefresh(true)
                    navigateToHome(navController)
                },
                showSnackBar = showSnackBar
            )
        }
        composable(
            route = UiScreen.ClientTimerScreen.route +
                    "?userEntity={userEntity}&hostEndpointId={hostEndpointId}",
            arguments = listOf(
                navArgument("userEntity") { type = NavType.StringType },
                navArgument("hostEndpointId") { type = NavType.StringType }
            )
        ) {
            ClientTimerRoute(
                navigateUp = {
                    gViewModel.setIsRefresh(true)
                    navigateToHome(navController)
                }
            )
        }
        composable(
            route = UiScreen.StaticsScreen.route + "?userEntity={userEntity}",
            arguments = listOf(navArgument("userEntity") { type = NavType.StringType })
        ) {
            StaticsRoute(
                navigateUp = navController::navigateUp
            )
        }
    }
}

@Composable
fun EmptyScreen() {
}

private fun navigateToStaticsScreen(
    navController: NavHostController,
    userEntity: UserEntity?
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)
        navController.navigate(UiScreen.StaticsScreen.route + "?userEntity=${userEntityJson}")
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
    timerCmInfo: TimerCommunicateInfo
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)
        val timerCmInfoJson = Json.encodeToString(TimerCommunicateInfo.serializer(), timerCmInfo)
            .replace("&", "%26")

        navController.navigate(
            UiScreen.HostTimerScreen.route +
                    "?&userEntity=${userEntityJson}&timerCmInfo=${timerCmInfoJson}"
        )
    }
}

private fun navigateToClientTimerScreen(
    navController: NavHostController,
    userEntity: UserEntity?,
    hostEndpointId: String
) {
    if (userEntity != null) {
        val userEntityJson = Json.encodeToString(UserEntity.serializer(), userEntity)

        navController.navigate(
            UiScreen.ClientTimerScreen.route +
                    "?&userEntity=${userEntityJson}&hostEndpointId=${hostEndpointId}"
        )
    }
}

private fun navigateToHome(navController: NavHostController) {
    navController.popBackStack(route = UiScreen.HomeScreen.route, inclusive = false)
}

private fun enterTransition() =
    fadeIn(animationSpec = tween(300)) +
            slideInHorizontally(
                animationSpec = tween(300)
            ) {
                it / 2
            }

private fun popEnterTransition() =
    fadeIn(animationSpec = tween(300)) +
            slideInHorizontally(
                animationSpec = tween(300)
            )

private fun exitTransition() =
    fadeOut(animationSpec = tween(300)) +
            slideOutHorizontally(
                animationSpec = tween(300)
            )

private fun popExitTransition() =
    fadeOut(animationSpec = tween(300)) +
            slideOutHorizontally(animationSpec = tween(300)) {
                it / 2
            }