package com.sghore.needtalk.presentation.ui.home_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.sghore.needtalk.domain.usecase.GetPagingTalkHistoryUseCase
import com.sghore.needtalk.domain.usecase.InitUserEntityUseCase
import com.sghore.needtalk.domain.usecase.InsertUserEntityUseCase
import com.sghore.needtalk.presentation.ui.DialogScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val initUserUseCase: InitUserEntityUseCase,
    private val insertUserUseCase: InsertUserEntityUseCase,
    private val getPagingTalkHistoryUseCase: GetPagingTalkHistoryUseCase
) : ViewModel() {

    private var _uiState = MutableStateFlow(
        HomeUiState(talkHistory = getPagingTalkHistoryUseCase().cachedIn(viewModelScope))
    )
    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()

    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        HomeUiState()
    )
    val uiEvent = _uiEvent.shareIn(
        viewModelScope,
        SharingStarted.Eagerly
    )

    // 유저 데이터 가져옴
    fun initUser(userId: String) = viewModelScope.launch {
        if (_uiState.value.user == null) {
            initUserUseCase(userId)
                .collectLatest { userEntity ->
                    _uiState.update {
                        it.copy(user = userEntity)
                    }
                    handelEvent(HomeUiEvent.UpdateUserEntity(userEntity))
                }
        }
    }

    // 보여줄 다이얼로그 화면 설정
    fun setDialogScreen(dialogScreen: DialogScreen) = viewModelScope.launch {
        _uiState.update {
            it.copy(dialogScreen = dialogScreen)
        }
    }

    fun clickStartAndClose() {
        _uiState.update {
            it.copy(isStart = !it.isStart)
        }
    }

    // 이벤트 처리
    fun handelEvent(event: HomeUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    // 유저 이름 수정
    fun updateUserName(updateUserName: String) = viewModelScope.launch {
        val user = _uiState.value.user

        if (updateUserName.isNotEmpty() && user != null) {
            insertUserUseCase(user.copy(name = updateUserName))
            handelEvent(HomeUiEvent.SuccessUpdateUserName)
        }
    }

    // 리스트 재구성
    fun refreshList() {
        _uiState.update {
            it.copy(talkHistory = getPagingTalkHistoryUseCase().cachedIn(viewModelScope))
        }
    }
}