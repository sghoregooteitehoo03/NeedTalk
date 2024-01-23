package com.sghore.needtalk.domain.usecase

import com.google.android.gms.nearby.connection.Payload
import com.sghore.needtalk.data.repository.NearByRepository
import javax.inject.Inject

class SendPayloadUseCase @Inject constructor(
    private val nearByRepository: NearByRepository
) {
    operator fun invoke(
        payload: Payload,
        endpointId: String,
        onFailure: (Exception) -> Unit
    ) = nearByRepository.sendPayload(payload, endpointId, onFailure)
}