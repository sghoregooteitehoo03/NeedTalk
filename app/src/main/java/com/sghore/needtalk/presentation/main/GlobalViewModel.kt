package com.sghore.needtalk.presentation.main

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.sghore.needtalk.data.model.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor() : ViewModel() {
    private var isRefresh by mutableStateOf(false)
    private var userEntity: UserEntity? = null

    fun setUserEntity(newUserEntity: UserEntity?) {
        userEntity = newUserEntity
    }

    fun getUserEntity() = userEntity

    fun setIsRefresh(refresh: Boolean) {
        isRefresh = refresh
    }

    fun getIsRefresh() = isRefresh
}