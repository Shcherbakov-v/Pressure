package com.mydoctor.pressure.di

import com.mydoctor.pressure.data.OfflinePressuresRepository
import com.mydoctor.pressure.data.PressuresRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * Module for Dependency injection.
 */
@Module
@InstallIn(ViewModelComponent::class)
abstract class PressuresDatabaseModule {

    /**
     * Provides instance of [OfflinePressuresRepository]
     */
    @ViewModelScoped
    @Binds
    internal abstract fun bindDatabasePressures(impl: OfflinePressuresRepository): PressuresRepository
}