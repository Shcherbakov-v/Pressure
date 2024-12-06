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
interface PressureDao {
    @Query("SELECT * FROM pressures ORDER BY date")
    fun getAllPressures(): Flow<List<Pressure>>

    @Query("SELECT * FROM pressures WHERE date BETWEEN :startDate AND :endDate ORDER BY date")
    fun getPressures(startDate: Long, endDate: Long): Flow<List<Pressure>>

    @Query("SELECT * from pressures WHERE id = :id")
    fun getPressure(id: Long): Flow<Pressure>

    // Specify the conflict strategy as IGNORE, when the user tries to add an
    // existing Item into the database Room ignores the conflict.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Pressure)

    @Delete
    suspend fun delete(item: Pressure)
}