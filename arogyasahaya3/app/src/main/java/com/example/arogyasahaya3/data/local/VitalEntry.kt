package com.example.arogyasahaya3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vitals")
data class VitalEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "BP", "Sugar", "HeartRate"
    val value1: Float, // Systolic for BP, Level for Sugar/HR
    val value2: Float = 0f, // Diastolic for BP
    val timestamp: Long = System.currentTimeMillis()
)
