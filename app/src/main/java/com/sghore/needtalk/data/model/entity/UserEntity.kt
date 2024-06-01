package com.sghore.needtalk.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


// TODO: 나중에 삭제하기
@Serializable
@Entity
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val name: String,
    val color: Int,
    val createTime: Long = System.currentTimeMillis()
)
