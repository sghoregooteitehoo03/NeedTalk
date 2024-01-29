package com.sghore.needtalk.di

import android.app.NotificationManager
import android.content.Context
import android.hardware.SensorManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped

@Module
@InstallIn(ServiceComponent::class)
object ServiceModule {
    @ServiceScoped
    @Provides
    fun provideNotificationManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    @ServiceScoped
    @Provides
    fun provideSensorManager(@ApplicationContext context: Context) =
        context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
}