package com.sghore.needtalk.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sghore.needtalk.domain.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor() : ViewModel() {
    private var userData: UserData? = null

    fun setUserData(newUserData: UserData?) {
        userData = newUserData
    }

    fun getUserData() = userData
}