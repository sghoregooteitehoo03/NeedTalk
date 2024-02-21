package com.sghore.needtalk.presentation.ui

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
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
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (result[Manifest.permission.ACCESS_COARSE_LOCATION] == false ||
                        result[Manifest.permission.POST_NOTIFICATIONS] == false ||
                        result[Manifest.permission.NEARBY_WIFI_DEVICES] == false
                    ) {
                        Toast.makeText(this, "권한을 모두 허용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (result[Manifest.permission.ACCESS_COARSE_LOCATION] == false) {
                        Toast.makeText(this, "권한이 모두 허용해주세요.", Toast.LENGTH_SHORT)
                            .show()
                        finish()
                    }
                }
            }
            val scaffoldState = rememberScaffoldState()

            NeedTalkTheme {
                val navController = rememberNavController()

                DisposableEffectWithLifeCycle(
                    onCreate = {
                        launcher.launch(getPermissions())
                    },
                    onDispose = {}
                )

                Scaffold(scaffoldState = scaffoldState) {
                    AppNavHost(
                        modifier = Modifier.padding(it),
                        gViewModel = gViewModel,
                        navController = navController,
                        showSnackBar = { message ->
                            scaffoldState.snackbarHostState.showSnackbar(message)
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

    private fun getPermissions() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.NEARBY_WIFI_DEVICES,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
}