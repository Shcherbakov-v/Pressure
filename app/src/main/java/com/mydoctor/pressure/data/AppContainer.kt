package com.mydoctor.pressure.data

import android.content.Context

/**
 * App container for Dependency injection.
 */
interface AppContainer {
    val pressuresRepository: PressuresRepository
}

/**
 * [AppContainer] implementation that provides instance of [OfflinePressuresRepository]
 */
class AppDataContainer(private val context: Context) : AppContainer {
    /**
     * Implementation for [PressuresRepository]
     */
    override val pressuresRepository: PressuresRepository by lazy {
        OfflinePressuresRepository(PressureRoomDatabase.getDatabase(context).pressureDao())
    }
}
