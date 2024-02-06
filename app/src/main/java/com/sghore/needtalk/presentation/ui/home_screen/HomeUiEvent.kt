package com.sghore.needtalk.presentation.ui.home_screen

import com.sghore.needtalk.data.model.entity.UserEntity

sealed interface HomeUiEvent {
    data object ClickNameTag : HomeUiEvent

    data object ClickStatics : HomeUiEvent

    data object SuccessUpdateUserName : HomeUiEvent

    data object ClickStartAndClose : HomeUiEvent

    data object ClickCreate : HomeUiEvent

    data object ClickJoin : HomeUiEvent

    data class UpdateUserEntity(val userEntity: UserEntity?) : HomeUiEvent
}