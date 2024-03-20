package com.sghore.needtalk.data.repository.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
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
    fun getTalkEntity(offset: Int, limit: Int = 5): Flow<List<TalkEntity>>

    @Query("SELECT * FROM TalkEntity WHERE createTimeStamp BETWEEN :startTime AND :endTime")
    suspend fun getTalkEntity(startTime: Long, endTime: Long): List<TalkEntity>

    @Insert
    suspend fun insertTalkEntity(talkEntity: TalkEntity)

    @Query("SELECT * FROM TalkTopicEntity WHERE groupCode == :groupCode")
    fun getTalkTopicEntity(groupCode: Int): Flow<List<TalkTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTalkTopicEntity(talkTopicEntity: TalkTopicEntity)

    @Delete
    suspend fun deleteTalkTopicEntity(talkTopicEntity: TalkTopicEntity)

    @Query("SELECT * FROM TimerSettingEntity")
    fun getTimerSettingEntity(): Flow<TimerSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTimerSettingEntity(timerSettingEntity: TimerSettingEntity)
}