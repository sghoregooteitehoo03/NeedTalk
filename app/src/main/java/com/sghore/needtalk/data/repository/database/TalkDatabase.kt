package com.sghore.needtalk.data.repository.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sghore.needtalk.data.model.MusicEntity
import com.sghore.needtalk.data.model.TalkEntity
import com.sghore.needtalk.data.model.TimerSettingEntity
import com.sghore.needtalk.data.model.UserEntity

@Database(
    entities = [UserEntity::class, TalkEntity::class, MusicEntity::class, TimerSettingEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TalkDatabase : RoomDatabase() {
    abstract fun getDao(): TalkDao
}