package com.sghore.needtalk.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.domain.model.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor() : ViewModel() {
    private var isRefresh by mutableStateOf(false)
    private var userData: UserData? = null
    private var userEntity: UserEntity? = null

    fun setUserData(newUserData: UserData?) {
        userData = newUserData
    }

    fun getUserData() = userData

    // TODO: 나중에 삭제
    fun getUserEntity() = userEntity

    fun setIsRefresh(refresh: Boolean) {
        isRefresh = refresh
    }

    fun getIsRefresh() = isRefresh
}