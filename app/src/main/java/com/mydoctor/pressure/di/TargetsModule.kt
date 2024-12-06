package com.mydoctor.pressure.di

import com.mydoctor.pressure.data.OfflineTargetsRepository
import com.mydoctor.pressure.data.TargetsRepository
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
abstract class TargetsDatabaseModule {

    /**
     * Provides instance of [OfflineTargetsRepository]
     */
    @ViewModelScoped
    @Binds
    internal abstract fun bindDatabaseTargets(impl: OfflineTargetsRepository): TargetsRepository
}
