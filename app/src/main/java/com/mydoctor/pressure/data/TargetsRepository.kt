package com.mydoctor.pressure.data

import kotlinx.coroutines.flow.Flow

/**
 * Repository that provides insert, delete, and retrieve of [Target] from a given data source.
 */
interface TargetsRepository {
    /**
     * Retrieve all the targets from the the given data source.
     */
    fun getAllTargetsStream(): Flow<List<Target>>

    /**
     * Retrieve a [Target] from the given data source that matches with the [id].
     */
    fun getTargetStream(id: Long): Flow<Target?>

    /**
     * Insert [Target] in the data source
     */
    suspend fun insertTarget(target: Target): Long

    /**
     * Delete [Target] from the data source
     */
    suspend fun deleteTarget(target: Target)
}