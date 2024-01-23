package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.NearByRepository
import javax.inject.Inject

class ConnectToHostUseCase @Inject constructor(
    private val nearByRepository: NearByRepository
) {
    operator fun invoke(userId: String, endpointId: String) =
        nearByRepository.connectToHost(userId, endpointId)
}