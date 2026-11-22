package com.lira.tracker.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.lira.tracker.data.model.ListenerRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ListenerDao {

    @Query("SELECT * FROM listener_records ORDER BY timestamp ASC")
    fun getAllAscending(): Flow<List<ListenerRecord>>

    @Query("SELECT * FROM listener_records ORDER BY timestamp DESC")
    fun getAllDescending(): Flow<List<ListenerRecord>>

    @Query("SELECT * FROM listener_records ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatest(): ListenerRecord?

    @Query("SELECT * FROM listener_records WHERE dateString = :dateString ORDER BY timestamp DESC LIMIT 1")
    suspend fun getByDate(dateString: String): ListenerRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: ListenerRecord): Long

    @Update
    suspend fun update(record: ListenerRecord)

    @Delete
    suspend fun delete(record: ListenerRecord)

    @Query("DELETE FROM listener_records")
    suspend fun clearAll()
}
