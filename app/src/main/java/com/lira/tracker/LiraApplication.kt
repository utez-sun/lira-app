package com.lira.tracker

import android.app.Application
import com.lira.tracker.data.db.AppDatabase
import com.lira.tracker.data.preferences.AppPreferences
import com.lira.tracker.data.repository.ArtistRepository
import com.lira.tracker.worker.SyncScheduler

class LiraApplication : Application() {

    lateinit var database: AppDatabase
        private set

    lateinit var preferences: AppPreferences
        private set

    lateinit var repository: ArtistRepository
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this

        database = AppDatabase.getInstance(this)
        preferences = AppPreferences(this)
        repository = ArtistRepository(database.listenerDao(), preferences)

        // Запуск планировщика ежедневного фонового обновления в 15:00 (или по настройкам)
        SyncScheduler.scheduleDailySync(this)
    }

    companion object {
        lateinit var instance: LiraApplication
            private set
    }
}
