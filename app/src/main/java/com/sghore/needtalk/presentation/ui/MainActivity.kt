package com.sghore.needtalk.presentation.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sghore.needtalk.presentation.ui.home_screen.HomeRoute
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NeedTalkTheme {
                val navController = rememberNavController()

                AppNavHost(
                    gViewModel = gViewModel,
                    navController = navController
                )
            }
        }
    }
}