package com.mydoctor.pressure.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RenameColumn
import androidx.room.RoomDatabase
import androidx.room.migration.AutoMigrationSpec

/**
 * Database class
 */
@Database(
    entities = [Pressure::class, Target::class],
    version = 3,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(
            from = 2,
            to = 3,
            spec = PressureRoomDatabase.RenameColumnAchievedAutoMigration::class
        )
    ]
)
abstract class PressureRoomDatabase : RoomDatabase() {
    abstract fun pressureDao(): PressureDao
    abstract fun targetDao(): TargetDao

    @RenameColumn(tableName = "targets", fromColumnName = "achieved", toColumnName = "accomplished")
    class RenameColumnAchievedAutoMigration : AutoMigrationSpec
}