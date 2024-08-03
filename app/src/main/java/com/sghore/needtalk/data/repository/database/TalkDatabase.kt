package com.sghore.needtalk.data.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.needtalk.data.model.entity.FriendEntity
import com.sghore.needtalk.data.model.entity.GroupSegmentEntity
import com.sghore.needtalk.data.model.entity.TalkTopicGroupEntity
import com.sghore.needtalk.data.model.entity.TalkHighlightEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryEntity
import com.sghore.needtalk.data.model.entity.TalkHistoryParticipantEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.model.entity.TalkSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity

@Database(
    entities = [
        UserEntity::class, FriendEntity::class, TalkTopicGroupEntity::class, TalkSettingEntity::class,
        TalkTopicEntity::class, GroupSegmentEntity::class, TalkHistoryEntity::class,
        TalkHistoryParticipantEntity::class, TalkHighlightEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class TalkDatabase : RoomDatabase() {
    abstract fun getTalkDao(): TalkDao
    abstract fun getTalkTopicDao(): TalkTopicDao
    abstract fun getUserDao(): UserDao
}