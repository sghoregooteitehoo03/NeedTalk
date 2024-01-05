package com.sghore.needtalk.data.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sghore.needtalk.data.model.TalkEntity
import com.sghore.needtalk.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TalkDao {
    @Query("SELECT * FROM UserEntity WHERE userId == :userId")
    fun getUserEntity(userId: String): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserEntity(userEntity: UserEntity)

    @Query(
        "SELECT * " +
                "FROM TalkEntity " +
                "ORDER BY createTimeStamp DESC " +
                "LIMIT :limit " +
                "OFFSET :offset"
    )
    fun getTalkHistory(offset: Int, limit: Int = 5): Flow<List<TalkEntity>>
}