package com.example.arogyasahaya3.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class Medicine(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dosage: String,
    val time: String,
    val morning: Boolean,
    val afternoon: Boolean,
    val night: Boolean,
    val notes: String
)
