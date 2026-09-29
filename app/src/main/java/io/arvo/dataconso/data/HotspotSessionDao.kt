package io.arvo.dataconso.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HotspotSessionDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(session: HotspotSessionEntity): Long

    @Update
    suspend fun update(session: HotspotSessionEntity)

    @Query("SELECT * FROM hotspot_sessions WHERE endTimestamp = 0 ORDER BY startTimestamp DESC LIMIT 1")
    suspend fun getActiveSession(): HotspotSessionEntity?

    @Query("SELECT * FROM hotspot_sessions WHERE endTimestamp = 0 ORDER BY startTimestamp DESC LIMIT 1")
    fun observeCurrentSession(): Flow<HotspotSessionEntity?>

    @Query("SELECT * FROM hotspot_sessions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): HotspotSessionEntity?

    @Query("SELECT * FROM hotspot_sessions WHERE startTimestamp = :startTimestamp AND sessionId = :sessionId LIMIT 1")
    suspend fun getBySyncKey(startTimestamp: Long, sessionId: String): HotspotSessionEntity?

    @Query("SELECT * FROM hotspot_sessions ORDER BY startTimestamp DESC")
    fun observeAllSessions(): Flow<List<HotspotSessionEntity>>

    @Query("SELECT * FROM hotspot_sessions WHERE startTimestamp >= :startTimestamp ORDER BY startTimestamp DESC")
    fun observeSessionsFrom(startTimestamp: Long): Flow<List<HotspotSessionEntity>>

    @Query("SELECT * FROM hotspot_sessions WHERE synced = 0 AND endTimestamp > 0 ORDER BY startTimestamp ASC")
    suspend fun getUnsyncedClosedSessions(): List<HotspotSessionEntity>

    @Query("UPDATE hotspot_sessions SET synced = 1, lastSyncedTimestamp = :syncedAt WHERE id = :id")
    suspend fun markSynced(id: Long, syncedAt: Long)
}
