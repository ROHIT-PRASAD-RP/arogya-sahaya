package com.example.arogyasahaya3.data.repository

import com.example.arogyasahaya3.data.local.Medicine
import com.example.arogyasahaya3.data.local.MedicineDao
import kotlinx.coroutines.flow.Flow

class MedicineRepository(private val medicineDao: MedicineDao) {
    val allMedicines: Flow<List<Medicine>> = medicineDao.getAllMedicines()

    suspend fun insert(medicine: Medicine) {
        medicineDao.insertMedicine(medicine)
    }

    suspend fun update(medicine: Medicine) {
        medicineDao.updateMedicine(medicine)
    }

    suspend fun delete(medicine: Medicine) {
        medicineDao.deleteMedicine(medicine)
    }
}
