package com.sghore.needtalk.presentation.ui.profile_screen

import androidx.compose.runtime.Composable
import com.sghore.needtalk.domain.model.UserData

@Composable
fun ProfileRoute(
    userData: UserData?
) {
    ProfileScreen(
        userData = userData
    )
}