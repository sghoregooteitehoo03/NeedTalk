package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.NearByRepository
import javax.inject.Inject

class StopAllConnectionUseCase @Inject constructor(
    private val nearByRepository: NearByRepository
) {
    operator fun invoke(stopCase: StopCase) {
        when (stopCase) {
            StopCase.StopConnections -> {
                nearByRepository.stopConnection()
            }

            StopCase.StopDiscovery -> {
                nearByRepository.stopDiscovery()
            }
        }
    }
}

sealed interface StopCase {
    data object StopConnections : StopCase
    data object StopDiscovery : StopCase
}