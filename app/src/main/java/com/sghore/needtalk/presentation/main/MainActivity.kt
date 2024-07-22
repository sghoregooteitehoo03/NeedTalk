package com.sghore.needtalk.presentation.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.ads.MobileAds
import com.sghore.needtalk.presentation.ui.theme.NeedTalkTheme
import com.sghore.needtalk.util.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val gViewModel by viewModels<GlobalViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createChannel() // Notification channel 생성
        adInitialize()

        setContent {
            val scaffoldState = rememberScaffoldState()

            NeedTalkTheme {
                val navController = rememberNavController()

                Scaffold(scaffoldState = scaffoldState) {
                    AppNavHost(
                        modifier = Modifier.padding(it),
                        gViewModel = gViewModel,
                        navController = navController,
                        showSnackBar = { message ->
                            scaffoldState.snackbarHostState.showSnackbar(message)
                        },
                        onShareIntent = { path ->
                            // TODO: fix. 저장 경로 바꿔서 다시 테스트
                            val uri = Uri.parse(path)
                            val intent = Intent(Intent.ACTION_SEND).apply {
                                type = "audio/*"
                                putExtra(Intent.EXTRA_STREAM, uri)
                            }

                            startActivity(Intent.createChooser(intent, "공유하기"))
                        }
                    )
                }
            }
        }
    }

    private fun adInitialize() {
        MobileAds.initialize(
            this
        ) {

        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timerNotify = "타이머"
            val defaultNotify = "알림"

            val channels = listOf(
                NotificationChannel(
                    Constants.TIMER_SERVICE_CHANNEL,
                    timerNotify,
                    NotificationManager.IMPORTANCE_LOW
                ),
                NotificationChannel(
                    Constants.DEFAULT_NOTIFY_CHANNEL,
                    defaultNotify,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(channels)
        }
    }
}