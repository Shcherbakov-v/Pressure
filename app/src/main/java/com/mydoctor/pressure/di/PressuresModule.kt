package com.mydoctor.pressure.di

import com.mydoctor.pressure.data.OfflinePressuresRepository
import com.mydoctor.pressure.data.PressuresRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
annotation class DatabasePressures

@InstallIn(SingletonComponent::class)
@Module
abstract class PressuresDatabaseModule {

    @DatabasePressures
    @Singleton
    @Binds
    abstract fun bindDatabaseLogger(impl: OfflinePressuresRepository): PressuresRepository
}

