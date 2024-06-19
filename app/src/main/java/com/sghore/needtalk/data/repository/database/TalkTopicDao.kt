package com.sghore.needtalk.data.repository.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sghore.needtalk.data.model.entity.GroupSegmentEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity2
import com.sghore.needtalk.data.model.entity.TalkTopicGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TalkTopicDao {
    @Query("SELECT * FROM TalkTopicEntity2 WHERE :talkTopicId = id")
    suspend fun getTalkTopicEntity(talkTopicId: String): TalkTopicEntity2

    @Query("SELECT * FROM TalkTopicGroupEntity ORDER BY editedTime DESC")
    fun getAllTalkTopicGroupEntities(): Flow<List<TalkTopicGroupEntity>>

    @Query("SELECT * FROM TalkTopicGroupEntity WHERE :groupId = id")
    suspend fun getTalkTopicGroupEntity(groupId: Int): TalkTopicGroupEntity?

    @Query("SELECT * FROM TalkTopicGroupEntity ORDER BY editedTime DESC LIMIT :limit OFFSET :offset")
    fun getTalkTopicGroupEntities(offset: Int, limit: Int): Flow<List<TalkTopicGroupEntity>>

    @Query("SELECT * FROM GroupSegmentEntity WHERE :groupId = groupId LIMIT :limit OFFSET :offset")
    suspend fun getGroupSegmentEntities(
        groupId: Int,
        offset: Int,
        limit: Int
    ): List<GroupSegmentEntity>

    @Query("SELECT * FROM GroupSegmentEntity WHERE :topicId = topicId")
    suspend fun getGroupSegmentEntities(topicId: String): List<GroupSegmentEntity>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalkTopicGroupEntity(groupEntity: TalkTopicGroupEntity)

    @Insert
    suspend fun insertTalkTopicEntity(talkTopicEntity: TalkTopicEntity2)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGroupSegmentEntity(groupSegmentEntity: GroupSegmentEntity)

    @Delete
    suspend fun deleteTalkTopicGroupEntity(groupEntity: TalkTopicGroupEntity)

    @Query("DELETE FROM GroupSegmentEntity WHERE :groupId = groupId AND :topicId = topicId")
    suspend fun deleteGroupSegmentEntity(groupId: Int, topicId: String)
}