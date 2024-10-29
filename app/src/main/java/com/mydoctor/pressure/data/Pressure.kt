package com.mydoctor.pressure.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pressures")
data class Pressure(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val systolic: Int,
    val diastolic: Int,
    val pulse: Int,
    val date: Long,
    val note: String,
)