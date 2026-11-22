package com.lira.tracker.worker

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.lira.tracker.data.preferences.AppPreferences
import java.util.Calendar
import java.util.concurrent.TimeUnit

object SyncScheduler {

    private const val WORK_NAME = "lira_daily_sync_work"

    fun scheduleDailySync(context: Context) {
        val preferences = AppPreferences(context)
        val targetHour = preferences.syncHour
        val targetMinute = preferences.syncMinute

        val now = Calendar.getInstance()
        val targetCalendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, targetHour)
            set(Calendar.MINUTE, targetMinute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Если заданное время сегодня уже прошло, переносим на завтра
        if (targetCalendar.before(now)) {
            targetCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val initialDelay = targetCalendar.timeInMillis - now.timeInMillis

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = OneTimeWorkRequestBuilder<DailySyncWorker>()
            .setConstraints(constraints)
            .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
            .addTag("lira_sync")
            .build()

        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            syncRequest
        )
    }

    fun cancelDailySync(context: Context) {
        WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
    }
}
