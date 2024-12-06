package com.mydoctor.pressure.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Qualifier

/**
 * Module for Dependency injection.
 */
@Module
@InstallIn(ViewModelComponent::class)
object DispatcherModule {

    /**
     * Provides instance of [Dispatchers.IO]
     */
    @IODispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}

@Retention(AnnotationRetention.BINARY)
@Qualifier
annotation class IODispatcher