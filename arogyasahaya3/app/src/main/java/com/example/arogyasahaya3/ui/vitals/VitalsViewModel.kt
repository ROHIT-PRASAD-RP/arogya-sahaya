package com.example.arogyasahaya3.ui.vitals

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyasahaya3.data.local.AppDatabase
import com.example.arogyasahaya3.data.local.VitalEntry
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VitalsViewModel(application: Application) : AndroidViewModel(application) {
    private val vitalDao = AppDatabase.getDatabase(application).vitalDao()

    val bpEntries = vitalDao.getRecentVitals("BP").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sugarEntries = vitalDao.getRecentVitals("Sugar").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val heartRateEntries = vitalDao.getRecentVitals("HeartRate").stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addVital(type: String, v1: Float, v2: Float = 0f) {
        viewModelScope.launch {
            vitalDao.insertVital(VitalEntry(type = type, value1 = v1, value2 = v2))
        }
    }
}
