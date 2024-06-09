package com.sghore.needtalk.data.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.needtalk.data.model.entity.TalkTopicGroupEntity
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity2
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity
import com.sghore.needtalk.data.model.entity.UserEntity2

@Database(
    entities = [
        UserEntity2::class, TalkTopicGroupEntity::class, UserEntity::class,
        TalkEntity::class, TalkTopicEntity::class, TimerSettingEntity::class,
        TalkTopicEntity2::class
    ],
    version = 3,
    exportSchema = true
)
abstract class TalkDatabase : RoomDatabase() {
    abstract fun getTalkDao(): TalkDao
    abstract fun getTalkTopicDao(): TalkTopicDao
    abstract fun getUserDao(): UserDao
}