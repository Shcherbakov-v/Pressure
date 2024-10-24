package com.mydoctor.pressure.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, delete, and retrieve of [Pressure] from a given data source.
 */
interface PressuresRepository {
    /**
     * Retrieve all the pressures from the the given data source.
     */
    fun getAllPressuresStream(): Flow<List<Pressure>>

    /**
     * Retrieve an pressure from the given data source that matches with the [id].
     */
    fun getPressureStream(id: Int): Flow<Pressure?>

    /**
     * Insert pressure in the data source
     */
    suspend fun insertPressure(pressure: Pressure)

    /**
     * Delete pressure from the data source
     */
    suspend fun deletePressure(pressure: Pressure)
}