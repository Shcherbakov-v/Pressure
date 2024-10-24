package com.mydoctor.pressure.di

import android.content.Context
import androidx.room.Room
import com.mydoctor.pressure.data.PressureDao
import com.mydoctor.pressure.data.PressureRoomDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): PressureRoomDatabase {
        return Room.databaseBuilder(
            appContext,
            PressureRoomDatabase::class.java,
            "pressure.db"
        ).build()
    }

    @Provides
    fun provideLogDao(database: PressureRoomDatabase): PressureDao {
        return database.pressureDao()
    }
}
