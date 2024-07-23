package com.sghore.needtalk.data.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sghore.needtalk.data.model.entity.FriendEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity WHERE userId == :userId")
    fun getUserEntity(userId: String): Flow<UserEntity?>

    @Query("SELECT * FROM FriendEntity WHERE userId == :userId")
    fun getFriendEntity(userId: String): Flow<FriendEntity?>

    @Query("SELECT * FROM FriendEntity ORDER BY createTime DESC")
    fun getAllFriendEntities(): Flow<List<FriendEntity>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserEntity(userEntity: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFriendEntity(friendEntity: FriendEntity)


    @Query("DELETE FROM FriendEntity WHERE userId == :userId")
    suspend fun deleteFriendEntity(userId: String)
}