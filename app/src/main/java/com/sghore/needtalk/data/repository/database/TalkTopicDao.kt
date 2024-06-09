package com.sghore.needtalk.data.repository.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sghore.needtalk.data.model.entity.TalkTopicEntity2
import com.sghore.needtalk.data.model.entity.TalkTopicGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TalkTopicDao {
    @Query("SELECT * FROM TalkTopicGroupEntity ORDER BY editedTime DESC LIMIT :limit OFFSET :offset")
    fun getTalkTopicGroupEntity(offset: Int, limit: Int): Flow<List<TalkTopicGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTalkTopicGroupEntity(groupEntity: TalkTopicGroupEntity)

    @Insert
    suspend fun insertTalkTopic(talkTopicEntity: TalkTopicEntity2)
}