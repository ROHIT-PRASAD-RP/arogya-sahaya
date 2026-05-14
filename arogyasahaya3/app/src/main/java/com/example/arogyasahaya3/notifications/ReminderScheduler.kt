package com.example.arogyasahaya3.notifications

import android.content.Context
import androidx.work.*
import com.example.arogyasahaya3.data.local.Medicine
import java.util.*
import java.util.concurrent.TimeUnit

object ReminderScheduler {
    fun scheduleReminders(context: Context, medicine: Medicine) {
        if (medicine.morning) scheduleForTime(context, medicine, 8)
        if (medicine.afternoon) scheduleForTime(context, medicine, 14)
        if (medicine.night) scheduleForTime(context, medicine, 21)
    }

    private fun scheduleForTime(context: Context, medicine: Medicine, hour: Int) {
        val workTag = "pill_${medicine.id}_$hour"
        
        val data = Data.Builder()
            .putString("medicine_name", medicine.name)
            .putString("dosage", medicine.dosage)
            .build()

        val delay = calculateDelay(hour)

        // Using PeriodicWorkRequest for daily repetition
        val request = PeriodicWorkRequestBuilder<MedicineNotificationWorker>(24, TimeUnit.HOURS)
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .addTag("pill_${medicine.id}") // Tag by ID for easier cancellation
            .addTag(workTag)
            .setInputData(data)
            .setBackoffCriteria(BackoffPolicy.LINEAR, 15, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            workTag,
            ExistingPeriodicWorkPolicy.UPDATE,
            request
        )
    }

    private fun calculateDelay(hour: Int): Long {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }
        if (target.before(now)) {
            target.add(Calendar.DAY_OF_YEAR, 1)
        }
        return target.timeInMillis - now.timeInMillis
    }

    fun cancelReminders(context: Context, medicineId: Int) {
        WorkManager.getInstance(context).cancelAllWorkByTag("pill_$medicineId")
    }
}
