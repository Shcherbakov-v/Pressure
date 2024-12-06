package com.mydoctor.pressure.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the [PressureRoomDatabase]
 */
@Dao
interface TargetDao {
    @Query("SELECT * FROM targets")
    fun getAllTargets(): Flow<List<Target>>

    @Query("SELECT * from targets WHERE id = :id")
    fun getTarget(id: Long): Flow<Target>

    // Specify the conflict strategy as REPLACE, when the user tries to add an
    // existing Item into the database Room
    // since this strategy will always insert a row even if there is a conflict.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: Target): Long

    @Delete
    suspend fun delete(item: Target)
}
