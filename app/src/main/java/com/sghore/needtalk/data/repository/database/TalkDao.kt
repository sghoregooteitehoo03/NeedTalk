package com.sghore.needtalk.data.repository.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TalkHighlightEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryParticipantEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.model.entity.TalkSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TalkDao {

    @Query("SELECT * FROM TalkHistoryEntity WHERE id == :id")
    fun getTalkHistory(id: String): Flow<TalkHistoryEntity?>

    @Query("SELECT * FROM TalkHistoryEntity ORDER BY createTimeStamp DESC LIMIT :limit OFFSET :offset")
    fun getTalkHistoryEntities(offset: Int, limit: Int = 20): Flow<List<TalkHistoryEntity>>

    @Query("SELECT * FROM TalkHistoryParticipantEntity WHERE talkHistoryId == :talkId")
    fun getTalkHistoryParticipantEntities(talkId: String): Flow<List<TalkHistoryParticipantEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalkHistoryEntity(talkHistoryEntity: TalkHistoryEntity)

    @Insert
    suspend fun insertTalkHistoryParticipantEntity(talkHistoryParticipantEntity: TalkHistoryParticipantEntity)

    @Query("DELETE FROM TalkHistoryEntity WHERE id == :id")
    suspend fun deleteTalkHistoryEntity(id: String)


    @Query("SELECT * FROM TalkHighlightEntity WHERE talkHistoryId == :talkHistoryId")
    fun getTalkHighlightEntities(talkHistoryId: String): Flow<List<TalkHighlightEntity?>>

    @Insert
    suspend fun insertTalkHighlightEntity(talkHighlightEntity: TalkHighlightEntity)

    @Query("DELETE FROM TalkHighlightEntity WHERE id == :id")
    suspend fun deleteTalkHighlightEntity(id: Int?)

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

    @Insert
    suspend fun insertTalkEntity(talkEntity: TalkEntity)

    @Query("SELECT * FROM TalkTopicEntity WHERE groupCode == :groupCode")
    fun getTalkTopicEntity(groupCode: Int): Flow<List<TalkTopicEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTalkTopicEntity(talkTopicEntity: TalkTopicEntity)

    @Delete
    suspend fun deleteTalkTopicEntity(talkTopicEntity: TalkTopicEntity)


    @Query("SELECT * FROM TalkSettingEntity")
    fun getTalkSettingEntity(): Flow<TalkSettingEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalkSettingEntity(talkSettingEntity: TalkSettingEntity)
}