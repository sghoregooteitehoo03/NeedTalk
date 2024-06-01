package com.sghore.needtalk.data.repository

import com.sghore.needtalk.data.model.entity.UserEntity2
import com.sghore.needtalk.data.repository.database.UserDao
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val userDao: UserDao
) {

    // 유저 아이디를 통해 유저 정보를 반환함
    fun getUserEntity(userId: String) = userDao.getUserEntity(userId)

    // 유저 정보 삽입
    suspend fun insertUserEntity(userEntity: UserEntity2) {
        userDao.insertUserEntity(userEntity)
    }
}