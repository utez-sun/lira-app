package com.lira.tracker.data.preferences

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AppPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("lira_preferences", Context.MODE_PRIVATE)

    var artistId: String
        get() = prefs.getString(KEY_ARTIST_ID, DEFAULT_ARTIST_ID) ?: DEFAULT_ARTIST_ID
        set(value) = prefs.edit().putString(KEY_ARTIST_ID, value).apply()

    var syncHour: Int
        get() = prefs.getInt(KEY_SYNC_HOUR, DEFAULT_SYNC_HOUR)
        set(value) = prefs.edit().putInt(KEY_SYNC_HOUR, value).apply()

    var syncMinute: Int
        get() = prefs.getInt(KEY_SYNC_MINUTE, DEFAULT_SYNC_MINUTE)
        set(value) = prefs.edit().putInt(KEY_SYNC_MINUTE, value).apply()

    var lastSyncTimestamp: Long
        get() = prefs.getLong(KEY_LAST_SYNC_TIMESTAMP, 0L)
        set(value) = prefs.edit().putLong(KEY_LAST_SYNC_TIMESTAMP, value).apply()

    var lastSyncStatus: String
        get() = prefs.getString(KEY_LAST_SYNC_STATUS, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LAST_SYNC_STATUS, value).apply()

    fun observeSettings(): Flow<SettingsData> = callbackFlow {
        val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, _ ->
            trySend(
                SettingsData(
                    artistId = artistId,
                    syncHour = syncHour,
                    syncMinute = syncMinute
                )
            )
        }
        // Отправляем текущее значение сразу
        trySend(
            SettingsData(
                artistId = artistId,
                syncHour = syncHour,
                syncMinute = syncMinute
            )
        )
        prefs.registerOnSharedPreferenceChangeListener(listener)
        awaitClose { prefs.unregisterOnSharedPreferenceChangeListener(listener) }
    }

    data class SettingsData(
        val artistId: String,
        val syncHour: Int,
        val syncMinute: Int
    )

    companion object {
        private const val KEY_ARTIST_ID = "pref_artist_id"
        private const val KEY_SYNC_HOUR = "pref_sync_hour"
        private const val KEY_SYNC_MINUTE = "pref_sync_minute"
        private const val KEY_LAST_SYNC_TIMESTAMP = "pref_last_sync_timestamp"
        private const val KEY_LAST_SYNC_STATUS = "pref_last_sync_status"

        const val DEFAULT_ARTIST_ID = "24578741"
        const val DEFAULT_SYNC_HOUR = 15
        const val DEFAULT_SYNC_MINUTE = 0
    }
}
