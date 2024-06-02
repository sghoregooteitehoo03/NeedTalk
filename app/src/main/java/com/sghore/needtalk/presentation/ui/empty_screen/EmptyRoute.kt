package com.sghore.needtalk.presentation.ui.empty_screen

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.sghore.needtalk.domain.model.UserData
import com.sghore.needtalk.presentation.ui.UiScreen
import com.sghore.needtalk.presentation.ui.permission_screen.getPermissions

@SuppressLint("HardwareIds")
@Composable
fun EmptyRoute(
    viewModel: EmptyViewModel = hiltViewModel(),
    onUpdateUserData: (UserData?) -> Unit,
    navigateOtherScreen: (route: String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        // 유저 정보 조회
        val id = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        val userData = viewModel.getUserData(userId = id)
        val isGranted = getPermissions().all { permission -> // 모든 조건이 만족하는지
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

        onUpdateUserData(userData)
        if (isGranted) { // 권한 설정이 되어 있다면
            if (userData != null) { // 유저 데이터 생성 여부 확인
                navigateOtherScreen(UiScreen.TalkHistoryScreen.route) // 대화기록 화면으로
            } else { // 유저 데이터가 존재하지 않을 시
                navigateOtherScreen(UiScreen.StartScreen.route) // 시작 화면으로
            }
        } else {
            navigateOtherScreen(UiScreen.PermissionScreen.route) // 권한이 설정되어 있지 않으면 권한 설정 화면 이동
        }
    }
}