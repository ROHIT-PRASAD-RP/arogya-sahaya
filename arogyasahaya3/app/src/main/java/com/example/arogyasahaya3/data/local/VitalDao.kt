package com.example.arogyasahaya3.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalDao {
    @Query("SELECT * FROM vitals WHERE type = :type ORDER BY timestamp DESC LIMIT 7")
    fun getRecentVitals(type: String): Flow<List<VitalEntry>>

    @Insert
    suspend fun insertVital(entry: VitalEntry)
}
