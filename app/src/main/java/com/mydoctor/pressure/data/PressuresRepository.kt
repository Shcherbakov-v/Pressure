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
     * Retrieve pressures from the specified data source that
     * correspond to the range from [startDate] to [endDate].
     */
    fun getPressuresStream(startDate: Long, endDate: Long): Flow<List<Pressure>>

    /**
     * Retrieve an [Pressure] from the given data source that matches with the [id].
     */
    fun getPressureStream(id: Long): Flow<Pressure?>

    /**
     * Insert [Pressure] in the data source
     */
    suspend fun insertPressure(pressure: Pressure)

    /**
     * Delete [Pressure] from the data source
     */
    suspend fun deletePressure(pressure: Pressure)
}