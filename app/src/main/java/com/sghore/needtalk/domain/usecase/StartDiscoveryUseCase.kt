package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.NearByRepository
import javax.inject.Inject

class StartDiscoveryUseCase @Inject constructor(
    private val nearByRepository: NearByRepository
) {
    operator fun invoke(packageName: String) =
        nearByRepository.startDiscovery(packageName)
}