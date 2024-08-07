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
import com.sghore.needtalk.domain.model.TimerCommunicateInfo
import com.sghore.needtalk.presentation.ui.UiScreen
import com.sghore.needtalk.presentation.ui.add_highlight_screen.AddHighlightRoute
import com.sghore.needtalk.presentation.ui.add_talktopic_screen.AddTalkTopicRoute
import com.sghore.needtalk.presentation.ui.create_profile_screen.CreateProfileRoute
import com.sghore.needtalk.presentation.ui.create_talk_screen.CreateTalkRoute
import com.sghore.needtalk.presentation.ui.empty_screen.EmptyRoute
import com.sghore.needtalk.presentation.ui.groups_detail_screen.GroupsDetailRoute
import com.sghore.needtalk.presentation.ui.home_screen.HomeScreen
import com.sghore.needtalk.presentation.ui.join_talk_screen.JoinTalkRoute
import com.sghore.needtalk.presentation.ui.permission_screen.PermissionRoute
import com.sghore.needtalk.presentation.ui.profile_screen.ProfileRoute
import com.sghore.needtalk.presentation.ui.result_screen.ResultRoute
import com.sghore.needtalk.presentation.ui.start_screen.StartRoute
import com.sghore.needtalk.presentation.ui.talk_topics_detail_screen.TalkTopicsDetailRoute
import com.sghore.needtalk.presentation.ui.talkhistory_detail_screen.TalkHistoryDetailRoute
import com.sghore.needtalk.presentation.ui.timer_screen.client_timer_screen.ClientTimerRoute
import com.sghore.needtalk.presentation.ui.timer_screen.host_timer_screen.HostTimerRoute
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.encodeToJsonElement

@Composable
fun AppNavHost(
    modifier: Modifier,
    gViewModel: GlobalViewModel,
    navController: NavHostController,
    showSnackBar: suspend (String) -> Unit,
    onShareIntent: (String) -> Unit
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = UiScreen.EmptyScreen.route,
        enterTransition = { fadeIn(tween(200)) },
        popEnterTransition = { fadeIn(tween(200)) },
        exitTransition = { fadeOut(tween(200)) },
        popExitTransition = { fadeOut(tween(200)) }
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
                navController.navigate(
                    UiScreen.CreateProfileScreen.route +
                            "?userId=${null}"
                )
            })
        }

        composable(
            UiScreen.CreateProfileScreen.route +
                    "?userId={userId}",
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                    nullable = true
                },
            )
        ) {
            CreateProfileRoute(
                onUpdateUserData = { userData, isCreated ->
                    gViewModel.setUserData(userData)
                    if (isCreated) {
                        navController.navigate(UiScreen.HomeScreen.route) {
                            popUpTo(0) { inclusive = true } // 모든 백스택 제거
                        }
                    } else {
                        navController.navigateUp()
                    }
                }
            )
        }

        composable(route = UiScreen.HomeScreen.route) {
            HomeScreen(
                gViewModel = gViewModel,
                navigateToOther = {
                    navController.navigate(route = it)
                }
            )
        }

        composable(
            route = UiScreen.TalkHistoryDetailScreen.route +
                    "?talkHistoryId={talkHistoryId}",
            arguments = listOf(
                navArgument("talkHistoryId") {
                    type = NavType.StringType
                }
            )
        ) {
            TalkHistoryDetailRoute(
                navigateUp = navController::navigateUp,
                navigateToAddHighlightScreen = { talkHistory ->
                    val id = talkHistory?.id ?: ""
                    val recordFilePath = talkHistory?.recordFile?.path ?: ""
                    val recordAmplitude = talkHistory
                        ?.recordAmplitude
                        ?.chunked(10)
                        ?.map { it.max() }
                        ?: emptyList()
                    val recordAmplitudeJson = Json.encodeToJsonElement(recordAmplitude)

                    navController.navigate(
                        UiScreen.AddHighlightScreen.route +
                                "?talkHistoryId=${id}&recordFilePath=${recordFilePath}&recordAmplitude=${recordAmplitudeJson}"
                    )
                },
                onShareIntent = onShareIntent
            )
        }

        composable(
            route = UiScreen.AddHighlightScreen.route +
                    "?talkHistoryId={talkHistoryId}&recordFilePath={recordFilePath}&recordAmplitude={recordAmplitude}",
            arguments = listOf(
                navArgument("talkHistoryId") {
                    type = NavType.StringType
                },
                navArgument("recordFilePath") {
                    type = NavType.StringType
                },
                navArgument("recordAmplitude") {
                    type = NavType.StringType
                },
            )
        ) {
            AddHighlightRoute(
                showSnackBar = showSnackBar,
                navigateUp = navController::navigateUp
            )
        }

        composable(route = UiScreen.ProfileScreen.route) {
            ProfileRoute(
                userData = gViewModel.getUserData(),
                navigateUp = navController::navigateUp,
                navigateToCreateProfile = {
                    navController.navigate(
                        UiScreen.CreateProfileScreen.route +
                                "?userId=${gViewModel.getUserData()?.userId}"
                    )
                }
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
            TalkTopicsDetailRoute(
                userData = gViewModel.getUserData(),
                navigateUp = { navController.navigateUp() }
            )
        }

        composable(route = UiScreen.GroupsDetailScreen.route) {
            GroupsDetailRoute(
                userData = gViewModel.getUserData(),
                navigateUp = navController::navigateUp,
                navigateToTalkTopicsDetailScreen = { navController.navigate(it) }
            )
        }

        composable(route = UiScreen.CreateTalkScreen.route) {
            CreateTalkRoute(
                userData = gViewModel.getUserData(),
                navigateUp = navController::navigateUp,
                navigateToTimer = { timerCmInfo ->
                    navigateToHostTimerScreen(
                        navController = navController,
                        timerCmInfo = timerCmInfo
                    )
                }
            )
        }
        composable(
            route = UiScreen.JoinTalkScreen.route +
                    "?packageName={packageName}",
            arguments = listOf(
                navArgument("packageName") { type = NavType.StringType },
            )
        ) {
            JoinTalkRoute(
                userData = gViewModel.getUserData(),
                navigateUp = navController::navigateUp,
                navigateToTimerScreen = { hostEndpointId ->
                    navigateToClientTimerScreen(
                        navController = navController,
                        hostEndpointId = hostEndpointId
                    )
                },
                showSnackBar = showSnackBar
            )
        }
        composable(
            route = UiScreen.HostTimerScreen.route +
                    "?timerCmInfo={timerCmInfo}",
            arguments = listOf(
                navArgument("timerCmInfo") { type = NavType.StringType }
            )
        ) {
            HostTimerRoute(
                userData = gViewModel.getUserData(),
                navigateUp = { navigateToHome(navController) },
                navigateResultScreen = {
                    navController.navigate(
                        route = it,
                        builder = {
                            popUpTo(UiScreen.HomeScreen.route) {
                                inclusive = false
                            }
                        })
                },
                showSnackBar = showSnackBar
            )
        }
        composable(
            route = UiScreen.ClientTimerScreen.route +
                    "?hostEndpointId={hostEndpointId}",
            arguments = listOf(
                navArgument("hostEndpointId") { type = NavType.StringType }
            )
        ) {
            ClientTimerRoute(
                userData = gViewModel.getUserData(),
                navigateUp = { navigateToHome(navController) },
                navigateResultScreen = {
                    navController.navigate(
                        route = it,
                        builder = {
                            popUpTo(UiScreen.HomeScreen.route) {
                                inclusive = false
                            }
                        })
                },
            )
        }
        composable(
            route = UiScreen.ResultScreen.route +
                    "?talkResult={talkResult}",
            arguments = listOf(navArgument("talkResult") { type = NavType.StringType })
        ) {
            ResultRoute(navigateUp = navController::navigateUp)
        }
    }
}

@Composable
fun EmptyScreen() {
}

private fun navigateToHostTimerScreen(
    navController: NavHostController,
    timerCmInfo: TimerCommunicateInfo
) {
    val timerCmInfoJson = Json.encodeToString(TimerCommunicateInfo.serializer(), timerCmInfo)
        .replace("&", "%26")

    navController.navigate(
        UiScreen.HostTimerScreen.route +
                "?timerCmInfo=${timerCmInfoJson}"
    )
}

private fun navigateToClientTimerScreen(
    navController: NavHostController,
    hostEndpointId: String
) {
    navController.navigate(
        UiScreen.ClientTimerScreen.route +
                "?hostEndpointId=${hostEndpointId}"
    )
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