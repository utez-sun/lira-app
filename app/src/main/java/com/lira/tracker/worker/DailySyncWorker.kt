package com.lira.tracker.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.lira.tracker.R
import com.lira.tracker.data.db.AppDatabase
import com.lira.tracker.data.preferences.AppPreferences
import com.lira.tracker.data.repository.ArtistRepository
import java.text.NumberFormat
import java.util.Locale

class DailySyncWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getInstance(context)
        val preferences = AppPreferences(context)
        val repository = ArtistRepository(database.listenerDao(), preferences)

        val result = repository.syncNow()

        return if (result.isSuccess) {
            val record = result.getOrThrow()
            showNotification(record.listeners, record.delta)
            // Планируем следующий запуск на завтра
            SyncScheduler.scheduleDailySync(context)
            Result.success()
        } else {
            if (runAttemptCount < 3) {
                Result.retry()
            } else {
                Result.failure()
            }
        }
    }

    private fun showNotification(listeners: Long, delta: Long) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "lira_daily_sync_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Обновления LIRA",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Ежедневное обновление числа слушателей на Яндекс Музыке"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val formattedListeners = NumberFormat.getNumberInstance(Locale.FRANCE).format(listeners)
        val deltaText = when {
            delta > 0 -> "+${NumberFormat.getNumberInstance(Locale.FRANCE).format(delta)}"
            delta < 0 -> "-${NumberFormat.getNumberInstance(Locale.FRANCE).format(kotlin.math.abs(delta))}"
            else -> "0"
        }

        val contentText = "$formattedListeners ($deltaText со вчера)"

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle("LIRA: Слушатели за месяц")
            .setContentText(contentText)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}
