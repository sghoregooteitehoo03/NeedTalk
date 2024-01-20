package com.sghore.needtalk.presentation.ui.join_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.DiscoveryEvent
import com.sghore.needtalk.domain.usecase.StartDiscoveryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class JoinViewModel @Inject constructor(
    private val startDiscoveryUseCase: StartDiscoveryUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var isFound by mutableStateOf(false)

    init {
        val userEntityJson = savedStateHandle.get<String>("userEntity")
        val packageName = savedStateHandle.get<String>("packageName") ?: ""

        if (userEntityJson != null && packageName.isNotEmpty()) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)
            startDiscovery(userEntity.userId, packageName)
        }
    }

    private fun startDiscovery(userId: String, packageName: String) = viewModelScope.launch {
        startDiscoveryUseCase(userId, packageName).collectLatest { event ->
            when (event) {
                is DiscoveryEvent.DiscoverySuccess -> {
                    Log.i("Check", "Discovery Success")
                }

                is DiscoveryEvent.EndpointFound -> {
                    isFound = true
                }

                else -> {}
            }
        }
    }
}