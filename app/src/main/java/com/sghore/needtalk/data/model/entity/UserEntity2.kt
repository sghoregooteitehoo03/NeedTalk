package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

// TODO: 나중에 클래스 이름 수정하기
@Entity
data class UserEntity2(
    @PrimaryKey
    val userId: String,
    val name: String,
    val profileImage: ByteArray,
    val createTime: Long = System.currentTimeMillis()
)
