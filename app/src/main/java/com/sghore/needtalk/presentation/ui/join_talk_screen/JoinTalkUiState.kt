package com.sghore.needtalk.presentation.ui.join_talk_screen

import com.sghore.needtalk.domain.model.TimerInfo

data class JoinUiState(
    val searchNearDevice: SearchNearDevice = SearchNearDevice.Searching(false)
)

sealed interface SearchNearDevice {
    data class Searching(val isFound: Boolean) : SearchNearDevice
    data class Load(val endpointIdList: List<String>, val timerInfoList: List<TimerInfo?>) :
        SearchNearDevice
}
