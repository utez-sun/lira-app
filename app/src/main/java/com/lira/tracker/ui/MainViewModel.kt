package com.lira.tracker.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lira.tracker.data.db.AppDatabase
import com.lira.tracker.data.model.ListenerRecord
import com.lira.tracker.data.preferences.AppPreferences
import com.lira.tracker.data.repository.ArtistRepository
import com.lira.tracker.worker.SyncScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getInstance(application)
    private val preferences = AppPreferences(application)
    private val repository = ArtistRepository(database.listenerDao(), preferences)

    val recordsAsc: StateFlow<List<ListenerRecord>> = repository.allRecordsAsc
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recordsDesc: StateFlow<List<ListenerRecord>> = repository.allRecordsDesc
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _artistId = MutableStateFlow(preferences.artistId)
    val artistId: StateFlow<String> = _artistId.asStateFlow()

    private val _syncHour = MutableStateFlow(preferences.syncHour)
    val syncHour: StateFlow<Int> = _syncHour.asStateFlow()

    private val _syncMinute = MutableStateFlow(preferences.syncMinute)
    val syncMinute: StateFlow<Int> = _syncMinute.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(preferences.lastSyncTimestamp)
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    fun clearUserMessage() {
        _userMessage.value = null
    }

    fun fetchNow() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.syncNow()
                if (result.isSuccess) {
                    val record = result.getOrThrow()
                    val numFormatted = NumberFormat.getNumberInstance(Locale.FRANCE).format(record.listeners)
                    val deltaFormatted = when {
                        record.delta > 0 -> "+${NumberFormat.getNumberInstance(Locale.FRANCE).format(record.delta)}"
                        record.delta < 0 -> "-${NumberFormat.getNumberInstance(Locale.FRANCE).format(kotlin.math.abs(record.delta))}"
                        else -> "0"
                    }
                    _lastSyncTimestamp.value = System.currentTimeMillis()
                    _userMessage.value = "Обновлено: $numFormatted ($deltaFormatted)"
                } else {
                    val err = result.exceptionOrNull()?.localizedMessage ?: "Ошибка связи"
                    _userMessage.value = "Не удалось обновить: $err"
                }
            } catch (e: Exception) {
                _userMessage.value = "Ошибка: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveSettings(newArtistId: String, newHour: Int, newMinute: Int) {
        preferences.artistId = newArtistId
        preferences.syncHour = newHour
        preferences.syncMinute = newMinute

        _artistId.value = newArtistId
        _syncHour.value = newHour
        _syncMinute.value = newMinute

        // Перепланируем фоновую работу
        SyncScheduler.scheduleDailySync(getApplication())

        val timeStr = "${newHour.toString().padStart(2, '0')}:${newMinute.toString().padStart(2, '0')}"
        _userMessage.value = "Настройки сохранены: ID $newArtistId, обновление в $timeStr"
    }
}
