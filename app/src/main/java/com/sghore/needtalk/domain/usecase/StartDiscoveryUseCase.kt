package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.NearByRepository
import javax.inject.Inject

class StartDiscoveryUseCase @Inject constructor(
    private val nearByRepository: NearByRepository
) {
    operator fun invoke(userId: String, packageName: String) =
        nearByRepository.startDiscovery(userId, packageName)
}