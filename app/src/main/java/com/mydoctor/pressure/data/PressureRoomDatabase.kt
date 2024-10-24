package com.mydoctor.pressure.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Pressure::class], version = 1, exportSchema = false)
abstract class PressureRoomDatabase : RoomDatabase() {

    abstract fun pressureDao(): PressureDao

    companion object {
        @Volatile
        private var Instance: PressureRoomDatabase? = null

        fun getDatabase(context: Context): PressureRoomDatabase {
            // if the Instance is not null, return it, otherwise create a new database instance.
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, PressureRoomDatabase::class.java, "pressure_database")
                    /**
                     * Setting this option in your app's database builder means that Room
                     * permanently deletes all data from the tables in your database when it
                     * attempts to perform a migration with no defined migration path.
                     */
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            }
        }
    }
}