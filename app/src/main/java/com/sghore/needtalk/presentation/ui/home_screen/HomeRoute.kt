package com.sghore.needtalk.presentation.ui.home_screen

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sghore.needtalk.presentation.ui.DisposableEffectWithLifeCycle

@SuppressLint("HardwareIds")
@Composable
fun HomeRoute(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DisposableEffectWithLifeCycle(
        onCreate = {
            val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
            viewModel.initUser(id)
        },
        onDispose = {}
    )

    Surface {
        HomeScreen(uiState = uiState)
    }
}