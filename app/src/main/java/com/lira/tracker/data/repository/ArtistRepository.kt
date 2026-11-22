package com.lira.tracker.data.repository

import com.lira.tracker.data.db.ListenerDao
import com.lira.tracker.data.model.ListenerRecord
import com.lira.tracker.data.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern

data class SyncResult(
    val record: ListenerRecord,
    val isAddedOrUpdated: Boolean,
    val message: String
)

class ArtistRepository(
    private val dao: ListenerDao,
    private val preferences: AppPreferences
) {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .followRedirects(true)
        .build()

    private val userAgent = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
            "(KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36"

    val allRecordsAsc: Flow<List<ListenerRecord>> = dao.getAllAscending()
    val allRecordsDesc: Flow<List<ListenerRecord>> = dao.getAllDescending()

    suspend fun getLatestRecord(): ListenerRecord? = dao.getLatest()

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        dao.clearAll()
    }

    suspend fun fetchCurrentListeners(artistId: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            // 1. Попытка через handler API Яндекс Музыки
            val handlerUrl = "https://music.yandex.ru/handlers/artist.jsx?artist=$artistId"
            val apiRequest = Request.Builder()
                .url(handlerUrl)
                .header("User-Agent", userAgent)
                .header("Accept", "application/json, text/javascript, */*; q=0.01")
                .header("X-Requested-With", "XMLHttpRequest")
                .header("Referer", "https://music.yandex.ru/artist/$artistId")
                .build()

            client.newCall(apiRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrEmpty()) {
                        val count = parseListenersFromJson(bodyString)
                        if (count != null && count > 0) {
                            return@withContext Result.success(count)
                        }
                    }
                }
            }

            // 2. Если API не вернул, парсим веб-страницу артиста
            val pageUrl = "https://music.yandex.ru/artist/$artistId"
            val pageRequest = Request.Builder()
                .url(pageUrl)
                .header("User-Agent", userAgent)
                .header("Accept-Language", "ru-RU,ru;q=0.9,en-US;q=0.8,en;q=0.7")
                .build()

            client.newCall(pageRequest).execute().use { response ->
                if (response.isSuccessful) {
                    val html = response.body?.string() ?: ""
                    val count = parseListenersFromHtml(html)
                    if (count != null && count > 0) {
                        return@withContext Result.success(count)
                    }
                }
            }

            Result.failure(Exception("Не удалось обнаружить число слушателей на странице артиста."))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun parseListenersFromJson(jsonString: String): Long? {
        return try {
            val root = JSONObject(jsonString)
            val artist = root.optJSONObject("artist")
            val stats = artist?.optJSONObject("stats") ?: root.optJSONObject("stats")
            val counts = artist?.optJSONObject("counts") ?: root.optJSONObject("counts")

            val value = stats?.optLong("lastMonthListeners", -1L) ?: -1L
            if (value > 0) return value

            val monthly = stats?.optLong("monthlyListeners", -1L) ?: -1L
            if (monthly > 0) return monthly

            val countListeners = counts?.optLong("listeners", -1L) ?: -1L
            if (countListeners > 0) return countListeners

            val directListeners = artist?.optLong("listeners", -1L) ?: -1L
            if (directListeners > 0) return directListeners

            null
        } catch (e: Exception) {
            null
        }
    }

    private fun parseListenersFromHtml(html: String): Long? {
        try {
            val jsonPattern = Pattern.compile("\"(?:lastMonthListeners|listeners|monthlyListeners)\"\\s*:\\s*(\\d+)")
            val jsonMatcher = jsonPattern.matcher(html)
            if (jsonMatcher.find()) {
                return jsonMatcher.group(1)?.toLongOrNull()
            }

            val textPattern = Pattern.compile("([\\d\\s\\u00a0]+)\\s*слушател[яейь]\\s*(?:за|в)\\s*месяц", Pattern.CASE_INSENSITIVE)
            val textMatcher = textPattern.matcher(html)
            if (textMatcher.find()) {
                val raw = textMatcher.group(1)?.replace("[\\s\\u00a0]".toRegex(), "")
                val parsed = raw?.toLongOrNull()
                if (parsed != null) return parsed
            }

            val altPattern = Pattern.compile("слушател[яейь]\\s*(?:за|в)\\s*месяц[^\\d]*([\\d\\s\\u00a0]+)", Pattern.CASE_INSENSITIVE)
            val altMatcher = altPattern.matcher(html)
            if (altMatcher.find()) {
                val raw = altMatcher.group(1)?.replace("[\\s\\u00a0]".toRegex(), "")
                val parsed = raw?.toLongOrNull()
                if (parsed != null) return parsed
            }
        } catch (e: Exception) {
            // Игнорируем ошибку парсинга
        }
        return null
    }

    suspend fun syncNow(): Result<SyncResult> = withContext(Dispatchers.IO) {
        val artistId = preferences.artistId
        val fetchResult = fetchCurrentListeners(artistId)

        if (fetchResult.isFailure) {
            val error = fetchResult.exceptionOrNull() ?: Exception("Неизвестная ошибка загрузки")
            preferences.lastSyncStatus = "Ошибка: ${error.localizedMessage}"
            return@withContext Result.failure(error)
        }

        val currentCount = fetchResult.getOrThrow()
        val now = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val todayStr = dateFormat.format(Date(now))

        val countFormatted = NumberFormat.getNumberInstance(Locale.FRANCE).format(currentCount)

        // Проверяем, есть ли уже запись за сегодня
        val todayRecord = dao.getByDate(todayStr)

        if (todayRecord != null) {
            // Если в текущий день данные уже получались и не отличаются — НЕ добавляем новую точку на график!
            if (todayRecord.listeners == currentCount) {
                preferences.lastSyncTimestamp = now
                preferences.lastSyncStatus = "Данные не изменились ($countFormatted)"
                return@withContext Result.success(
                    SyncResult(
                        record = todayRecord,
                        isAddedOrUpdated = false,
                        message = "Данные за сегодня уже зафиксированы и не изменились ($countFormatted сл.)"
                    )
                )
            } else {
                // Если за сегодня число изменилось, обновляем существующую запись сегодняшнего дня
                val previousRecords = dao.getAllDescending()
                val latestBeforeToday = dao.getLatest()
                val delta = if (latestBeforeToday != null && latestBeforeToday.id != todayRecord.id) {
                    currentCount - latestBeforeToday.listeners
                } else {
                    currentCount - todayRecord.listeners
                }

                val updatedRecord = todayRecord.copy(
                    timestamp = now,
                    listeners = currentCount,
                    delta = delta
                )
                dao.update(updatedRecord)

                preferences.lastSyncTimestamp = now
                preferences.lastSyncStatus = "Обновлено за сегодня: $countFormatted"

                val deltaStr = when {
                    delta > 0 -> "+${NumberFormat.getNumberInstance(Locale.FRANCE).format(delta)}"
                    delta < 0 -> "-${NumberFormat.getNumberInstance(Locale.FRANCE).format(kotlin.math.abs(delta))}"
                    else -> "0"
                }

                return@withContext Result.success(
                    SyncResult(
                        record = updatedRecord,
                        isAddedOrUpdated = true,
                        message = "Показатель за сегодня обновлен: $countFormatted ($deltaStr)"
                    )
                )
            }
        }

        // Если за сегодня записей еще нет — создаем новую запись
        val latestRecord = dao.getLatest()
        val delta = if (latestRecord != null) {
            currentCount - latestRecord.listeners
        } else {
            0L
        }

        val newRecord = ListenerRecord(
            dateString = todayStr,
            timestamp = now,
            listeners = currentCount,
            delta = delta
        )

        dao.insert(newRecord)
        preferences.lastSyncTimestamp = now
        preferences.lastSyncStatus = "Зафиксировано: $countFormatted"

        val deltaStr = when {
            delta > 0 -> "+${NumberFormat.getNumberInstance(Locale.FRANCE).format(delta)}"
            delta < 0 -> "-${NumberFormat.getNumberInstance(Locale.FRANCE).format(kotlin.math.abs(delta))}"
            else -> "0"
        }

        Result.success(
            SyncResult(
                record = newRecord,
                isAddedOrUpdated = true,
                message = "Новая точка зафиксирована: $countFormatted ($deltaStr)"
            )
        )
    }
}
