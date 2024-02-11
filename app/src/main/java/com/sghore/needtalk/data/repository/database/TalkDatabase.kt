package com.sghore.needtalk.data.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.needtalk.data.model.entity.TalkEntity
import com.sghore.needtalk.data.model.entity.TimerSettingEntity
import com.sghore.needtalk.data.model.entity.UserEntity

@Database(
    entities = [UserEntity::class, TalkEntity::class, TimerSettingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TalkDatabase : RoomDatabase() {
    abstract fun getDao(): TalkDao
}