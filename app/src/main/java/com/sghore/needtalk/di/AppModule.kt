package com.sghore.needtalk.di

import android.content.Context
import androidx.room.Room
import com.google.android.gms.nearby.Nearby
import com.sghore.needtalk.data.repository.database.TalkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideTalkDatabase(@ApplicationContext context: Context) =
        Room.databaseBuilder(
            context,
            TalkDatabase::class.java,
            "TalkDB"
        )
            .createFromAsset("DefaultTalkTopic.db")
            .build()

    @Singleton
    @Provides
    fun provideTalkDao(database: TalkDatabase) =
        database.getDao()

    @Singleton
    @Provides
    fun provideConnectionsClient(@ApplicationContext context: Context) =
        Nearby.getConnectionsClient(context)
}