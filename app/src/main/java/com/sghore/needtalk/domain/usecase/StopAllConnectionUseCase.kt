package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.NearByRepository
import javax.inject.Inject

class StopAllConnectionUseCase @Inject constructor(
    private val nearByRepository: NearByRepository
) {
    operator fun invoke(stopCase: StopCase) {
        when (stopCase) {
            is StopCase.DisconnectOther -> {
                nearByRepository.disconnectOther(stopCase.endpointId)
            }

            is StopCase.StopConnections -> {
                nearByRepository.stopAllEndpoints()
            }

            is StopCase.StopDiscovery -> {
                nearByRepository.stopDiscovery()
            }
        }
    }
}

sealed interface StopCase {
    data class DisconnectOther(val endpointId: String) : StopCase
    data object StopConnections : StopCase
    data object StopDiscovery : StopCase
}