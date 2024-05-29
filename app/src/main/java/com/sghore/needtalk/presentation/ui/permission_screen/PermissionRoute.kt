package com.sghore.needtalk.presentation.ui.permission_screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// TODO: 확인 버튼 누를 시 캐릭터 생성 여부에 따라 어디로 보낼 지 정하기

@Composable
fun PermissionRoute(
    navigateToStartScreen: () -> Unit,
) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (result[Manifest.permission.ACCESS_COARSE_LOCATION] == false ||
                result[Manifest.permission.POST_NOTIFICATIONS] == false ||
                result[Manifest.permission.NEARBY_WIFI_DEVICES] == false
            ) {
                Toast.makeText(context, "권한을 모두 허용해주세요.", Toast.LENGTH_SHORT)
                    .show()
                openAppSettings(context)
            } else {
                navigateToStartScreen()
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (result[Manifest.permission.ACCESS_COARSE_LOCATION] == false) {
                Toast.makeText(context, "권한을 모두 허용해주세요.", Toast.LENGTH_SHORT)
                    .show()
            } else {
                navigateToStartScreen()
            }
        }
    }

    PermissionScreen(
        onClickConfirm = { launcher.launch(getPermissions()) }
    )
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


// 앱 설정 화면으로 이동
private fun openAppSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
    }
    context.startActivity(intent)
}