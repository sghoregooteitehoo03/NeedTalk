package com.sghore.needtalk.data.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sghore.needtalk.data.model.entity.UserEntity2
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity2 WHERE userId == :userId")
    fun getUserEntity(userId: String): Flow<UserEntity2?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserEntity(userEntity: UserEntity2)
}