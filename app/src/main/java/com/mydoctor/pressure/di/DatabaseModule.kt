package com.mydoctor.pressure.di

import android.content.Context
import androidx.room.Room
import com.mydoctor.pressure.data.PressureDao
import com.mydoctor.pressure.data.PressureRoomDatabase
import com.mydoctor.pressure.data.TargetDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Module for Dependency injection.
 */
@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    /**
     * Provides instance of [PressureRoomDatabase]
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): PressureRoomDatabase {
        return Room.databaseBuilder(
            appContext,
            PressureRoomDatabase::class.java,
            "pressure.db"
        ).build()
    }

    /**
     * Provides instance of [PressureDao]
     */
    @Provides
    fun providePressureDao(database: PressureRoomDatabase): PressureDao {
        return database.pressureDao()
    }

    /**
     * Provides instance of [TargetDao]
     */
    @Provides
    fun provideTargetDao(database: PressureRoomDatabase): TargetDao {
        return database.targetDao()
    }
}
