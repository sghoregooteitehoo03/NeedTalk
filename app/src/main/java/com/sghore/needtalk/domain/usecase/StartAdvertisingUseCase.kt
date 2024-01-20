package com.sghore.needtalk.domain.usecase

import com.sghore.needtalk.data.repository.NearByRepository
import com.sghore.needtalk.data.repository.TalkRepository
import javax.inject.Inject

class StartAdvertisingUseCase @Inject constructor(
    private val nearByRepository: NearByRepository
) {
    operator fun invoke(userName: String, packageName: String) =
        nearByRepository.startAdvertising(userName, packageName)
}