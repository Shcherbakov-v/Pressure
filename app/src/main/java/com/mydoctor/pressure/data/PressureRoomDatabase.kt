package com.mydoctor.pressure.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Pressure::class], version = 1, exportSchema = false)
abstract class PressureRoomDatabase : RoomDatabase() {
    abstract fun pressureDao(): PressureDao
}
