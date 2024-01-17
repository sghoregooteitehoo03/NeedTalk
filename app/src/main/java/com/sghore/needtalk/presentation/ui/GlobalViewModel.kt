package com.sghore.needtalk.presentation.ui

import androidx.lifecycle.ViewModel
import com.sghore.needtalk.data.model.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor() : ViewModel() {
    private var userEntity: UserEntity? = null

    fun setUserEntity(newUserEntity: UserEntity?) {
        userEntity = newUserEntity
    }

    fun getUserEntity() = userEntity
}