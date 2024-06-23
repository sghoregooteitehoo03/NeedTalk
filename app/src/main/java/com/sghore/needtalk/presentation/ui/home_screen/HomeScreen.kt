package com.sghore.needtalk.presentation.ui.home_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.sghore.needtalk.R
import com.sghore.needtalk.presentation.main.GlobalViewModel
import com.sghore.needtalk.presentation.ui.DialogScreen
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle
import com.sghore.needtalk.presentation.ui.ProfileImage
import com.sghore.needtalk.presentation.ui.UiScreen
import com.sghore.needtalk.presentation.ui.home_screen.talk_history_screen.TalkHistoryRoute
import com.sghore.needtalk.presentation.ui.home_screen.talk_topics_screen.TalkTopicsRoute
import com.sghore.needtalk.presentation.ui.theme.Orange50

@Composable
fun HomeScreen(
    gViewModel: GlobalViewModel,
    navigateToOther: (route: String) -> Unit
) {
    val navController = rememberNavController()
    val userData = gViewModel.getUserData()!!
    var dialogScreen by remember { mutableStateOf<DialogScreen>(DialogScreen.DialogDismiss) }
    val systemUiController = rememberSystemUiController()
    val statusbarColor = MaterialTheme.colors.background

    DisposableEffectWithLifeCycle(
        onCreate = {
            systemUiController.setStatusBarColor(
                color = statusbarColor,
                darkIcons = true
            )
        },
        onDispose = {}
    )

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .padding(start = 14.dp, end = 14.dp)
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.clickable { navigateToOther(UiScreen.ProfileScreen.route) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ProfileImage(
                        backgroundSize = 32.dp,
                        imageSize = 24.dp,
                        profileImage = userData.profileImage
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = userData.name,
                        style = MaterialTheme.typography.h5.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                }
            }
        },
        bottomBar = {
            val backStack by navController.currentBackStackEntryAsState()
            val currentDestination = backStack?.destination
            val bottomScreenList = remember {
                listOf(UiScreen.TalkHistoryScreen, UiScreen.Nothing, UiScreen.TalkTopicsScreen)
            }
            val width = LocalConfiguration.current
                .screenWidthDp
                .minus(80)
                .div(2)
                .dp

            BottomNavigation {
                bottomScreenList.forEach { screen ->
                    if (screen == UiScreen.Nothing) {
                        Box(
                            modifier = Modifier
                                .height(56.dp)
                                .width(80.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(
                                        color = MaterialTheme.colors.secondary,
                                        shape = CircleShape
                                    )
                                    .clickable { dialogScreen = DialogScreen.DialogStart },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = R.drawable.ic_add),
                                    contentDescription = "Add",
                                    tint = MaterialTheme.colors.onSecondary
                                )
                            }
                        }
                    } else {
                        val selected =
                            currentDestination?.hierarchy?.any { it.route == screen.route } == true

                        BottomNavigationItem(
                            modifier = Modifier.width(width),
                            selected = selected,
                            label = {
                                Text(
                                    text = screen.bottomName,
                                    style = MaterialTheme.typography.subtitle1.copy(
                                        color = if (selected) {
                                            MaterialTheme.colors.secondary
                                        } else {
                                            colorResource(id = R.color.gray)
                                        },
                                        fontWeight = if (selected) {
                                            FontWeight.Bold
                                        } else {
                                            FontWeight.Medium
                                        }
                                    )
                                )
                            },
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    modifier = Modifier.size(24.dp),
                                    painter = painterResource(id = screen.bottomIcon),
                                    contentDescription = screen.route,
                                    tint = if (selected) {
                                        MaterialTheme.colors.secondary
                                    } else {
                                        colorResource(id = R.color.gray)
                                    }
                                )
                            })
                    }
                }
            }
        }
    ) {
        NavHost(
            modifier = Modifier.padding(it),
            navController = navController,
            startDestination = UiScreen.TalkHistoryScreen.route
        ) {
            composable(UiScreen.TalkHistoryScreen.route) {
                TalkHistoryRoute()
            }

            composable(route = UiScreen.TalkTopicsScreen.route) {
                TalkTopicsRoute(
                    userData = gViewModel.getUserData(),
                    navigateToOther = navigateToOther
                )
            }
        }
    }

    when (dialogScreen) {
        is DialogScreen.DialogStart -> {
            StartDialog(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colors.background,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .padding(14.dp),
                onDismiss = { dialogScreen = DialogScreen.DialogDismiss },
                onClickCreate = {
                    navigateToOther(UiScreen.CreateTalkScreen.route)
                },
                onClickJoin = {
                    navigateToOther(UiScreen.JoinTalkScreen.route)
                }
            )
        }

        else -> {}
    }
}

@Composable
fun StartDialog(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onClickCreate: () -> Unit,
    onClickJoin: () -> Unit
) {
    BottomSheetDialog(onDismissRequest = onDismiss) {
        Column(modifier = modifier) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    modifier = Modifier.align(Alignment.Center),
                    text = "시작하기",
                    style = MaterialTheme.typography.h5.copy(
                        color = MaterialTheme.colors.onPrimary,
                        fontWeight = FontWeight.Bold
                    )
                )
                Icon(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .align(Alignment.CenterEnd)
                        .clickable { onDismiss() },
                    painter = painterResource(id = R.drawable.ic_close),
                    contentDescription = "Close"
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .shadow(2.dp, MaterialTheme.shapes.medium)
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = MaterialTheme.shapes.medium
                        )
                        .fillMaxWidth(0.5f)
                        .clickable {
                            onDismiss()
                            onClickCreate()
                        }
                        .padding(top = 20.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(id = R.drawable.ic_create),
                        contentDescription = "CreateTalk"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "대화방 생성",
                        style = MaterialTheme.typography.h5.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(
                    modifier = Modifier
                        .shadow(2.dp, MaterialTheme.shapes.medium)
                        .clip(MaterialTheme.shapes.medium)
                        .background(
                            color = MaterialTheme.colors.background,
                            shape = MaterialTheme.shapes.medium
                        )
                        .fillMaxWidth()
                        .clickable {
                            onDismiss()
                            onClickJoin()
                        }
                        .padding(top = 20.dp, bottom = 20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        modifier = Modifier.size(48.dp),
                        painter = painterResource(id = R.drawable.ic_join),
                        contentDescription = "JoinTalk"
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "대화방 참가",
                        style = MaterialTheme.typography.h5.copy(
                            color = MaterialTheme.colors.onPrimary
                        )
                    )
                }
            }
        }
    }
}