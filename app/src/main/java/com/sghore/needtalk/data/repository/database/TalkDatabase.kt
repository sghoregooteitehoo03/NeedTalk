package com.sghore.needtalk.data.repository.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TalkTopicEntity
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity

@Database(
    entities = [UserEntity::class, TalkEntity::class, TalkTopicEntity::class, TimerSettingEntity::class],
    version = 3,
    exportSchema = true
)
abstract class TalkDatabase : RoomDatabase() {
    abstract fun getDao(): TalkDao
}