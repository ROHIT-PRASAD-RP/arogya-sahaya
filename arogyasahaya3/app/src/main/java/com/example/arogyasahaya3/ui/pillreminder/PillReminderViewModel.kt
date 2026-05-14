package com.example.arogyasahaya3.ui.pillreminder

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.arogyasahaya3.data.local.AppDatabase
import com.example.arogyasahaya3.data.local.Medicine
import com.example.arogyasahaya3.data.repository.MedicineRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PillReminderViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MedicineRepository
    val allMedicines: StateFlow<List<Medicine>>

    init {
        val medicineDao = AppDatabase.getDatabase(application).medicineDao()
        repository = MedicineRepository(medicineDao)
        allMedicines = repository.allMedicines.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun insert(medicine: Medicine) = viewModelScope.launch {
        repository.insert(medicine)
        com.example.arogyasahaya3.notifications.ReminderScheduler.scheduleReminders(getApplication(), medicine)
    }

    fun update(medicine: Medicine) = viewModelScope.launch {
        repository.update(medicine)
        com.example.arogyasahaya3.notifications.ReminderScheduler.cancelReminders(getApplication(), medicine.id)
        com.example.arogyasahaya3.notifications.ReminderScheduler.scheduleReminders(getApplication(), medicine)
    }

    fun delete(medicine: Medicine) = viewModelScope.launch {
        repository.delete(medicine)
        com.example.arogyasahaya3.notifications.ReminderScheduler.cancelReminders(getApplication(), medicine.id)
    }
}
