package com.mydoctor.pressure.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Target entity data class represents a single row in the database.
 */
@Entity(tableName = "targets")
data class Target(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val description: String,
    val date: Long,
    val accomplished: Boolean,
)