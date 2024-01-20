package com.sghore.needtalk.presentation.ui.host_timer_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.repository.ConnectionEvent
import com.sghore.needtalk.domain.usecase.StartAdvertisingUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import javax.inject.Inject

@HiltViewModel
class HostTimerViewModel @Inject constructor(
    private val startAdvertisingUseCase: StartAdvertisingUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var isFound by mutableStateOf(false)

    init {
        val userEntityJson = savedStateHandle.get<String>("userEntity")
        val packageName = savedStateHandle.get<String>("packageName") ?: ""

        if (userEntityJson != null && packageName.isNotEmpty()) {
            val userEntity = Json.decodeFromString(UserEntity.serializer(), userEntityJson)
            startAdvertising(userEntity.userId, packageName)
        }
    }

    private fun startAdvertising(userId: String, packageName: String) = viewModelScope.launch {
        startAdvertisingUseCase(userId, packageName)
            .collectLatest { event ->
                when (event) {
                    is ConnectionEvent.AdvertisingSuccess -> {
                        Log.i("Check", "Advertising Success")
                    }

                    is ConnectionEvent.ConnectionInitiated -> {
                        isFound = true
                    }

                    else -> {}
                }
            }
    }
}