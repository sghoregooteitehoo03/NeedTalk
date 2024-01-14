package com.sghore.needtalk.domain.usecase

import androidx.compose.ui.graphics.toArgb
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.presentation.ui.theme.Blue
import com.sghore.needtalk.presentation.ui.theme.Green
import com.sghore.needtalk.presentation.ui.theme.Orange
import com.sghore.needtalk.presentation.ui.theme.Pink
import com.sghore.needtalk.presentation.ui.theme.Purple
import com.sghore.needtalk.presentation.ui.theme.Red
import com.sghore.needtalk.presentation.ui.theme.Sky
import com.sghore.needtalk.presentation.ui.theme.Yellow
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class InitUserEntityUseCase @Inject constructor(
    private val getUserUseCase: GetUserEntityUseCase,
    private val insertUserUseCase: InsertUserEntityUseCase
) {
    operator fun invoke(userId: String) = getUserUseCase(userId)
        .onEach { userEntity ->
            if (userEntity == null) { // 유저 정보가 없으면 새로 만듬
                val user = createUser(userId)
                insertUserUseCase(user)
            }
        }

    private fun createUser(userId: String): UserEntity {
        val colorList = listOf(
            Red,
            Orange,
            Yellow,
            Green,
            Sky,
            Blue,
            Purple,
            Pink
        )

        return UserEntity(
            userId = userId,
            name = "닉네임",
            color = colorList.random().toArgb()
        )
    }
}