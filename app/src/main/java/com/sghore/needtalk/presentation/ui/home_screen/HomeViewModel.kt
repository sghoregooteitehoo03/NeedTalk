package com.sghore.needtalk.presentation.ui.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sghore.needtalk.domain.usecase.InitUserEntityUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val initUserUseCase: InitUserEntityUseCase
) : ViewModel() {

    private var _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        HomeUiState()
    )

    fun initUser(userId: String) = viewModelScope.launch {
        if (_uiState.value.user == null) {
            initUserUseCase(userId)
                .collectLatest { userEntity ->
                    _uiState.update {
                        it.copy(user = userEntity)
                    }
                }
        }
    }
}