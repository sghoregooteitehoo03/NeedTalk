package com.sghore.needtalk.di

import android.app.NotificationManager
import android.content.Context
import androidx.room.Room
import com.google.android.gms.nearby.Nearby
import com.sghore.needtalk.data.repository.database.TalkDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
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
        ).build()

    @Singleton
    @Provides
    fun provideTalkDao(database: TalkDatabase) =
        database.getDao()

    @Singleton
    @Provides
    fun provideRetrofit() =
        Retrofit.Builder()
            .client(OkHttpClient.Builder().apply {
                readTimeout(2, TimeUnit.MINUTES)
            }.build())
            .addConverterFactory(GsonConverterFactory.create())

    @Singleton
    @Provides
    fun provideConnectionsClient(@ApplicationContext context: Context) =
        Nearby.getConnectionsClient(context)

    @Singleton
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
}