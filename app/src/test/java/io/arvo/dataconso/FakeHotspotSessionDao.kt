package io.arvo.dataconso

import io.arvo.dataconso.data.HotspotSessionDao
import io.arvo.dataconso.data.HotspotSessionEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

internal class FakeHotspotSessionDao : HotspotSessionDao {
    private val sessions = mutableListOf<HotspotSessionEntity>()
    private val revision = MutableStateFlow(0)
    private var nextId = 1L

    override suspend fun insert(session: HotspotSessionEntity): Long {
        if (sessions.any { it.sessionId == session.sessionId }) return -1
        val id = if (session.id == 0L) nextId++ else session.id
        sessions += session.copy(id = id)
        changed()
        return id
    }

    override suspend fun update(session: HotspotSessionEntity) {
        val index = sessions.indexOfFirst { it.id == session.id }
        if (index >= 0) {
            sessions[index] = session
            changed()
        }
    }

    override suspend fun getActiveSession(): HotspotSessionEntity? = activeSession()

    private fun activeSession(): HotspotSessionEntity? =
        sessions.filter { it.endTimestamp == 0L }.maxByOrNull { it.startTimestamp }

    override fun observeCurrentSession(): Flow<HotspotSessionEntity?> =
        revision.map { activeSession() }

    override suspend fun getById(id: Long): HotspotSessionEntity? =
        sessions.firstOrNull { it.id == id }

    override suspend fun getBySyncKey(
        startTimestamp: Long,
        sessionId: String
    ): HotspotSessionEntity? = sessions.firstOrNull {
        it.startTimestamp == startTimestamp && it.sessionId == sessionId
    }

    override fun observeAllSessions(): Flow<List<HotspotSessionEntity>> =
        revision.map { sessions.sortedByDescending { session -> session.startTimestamp }.toList() }

    override fun observeSessionsFrom(startTimestamp: Long): Flow<List<HotspotSessionEntity>> =
        revision.map {
            sessions.filter { session -> session.startTimestamp >= startTimestamp }
                .sortedByDescending { session -> session.startTimestamp }
        }

    override suspend fun getUnsyncedClosedSessions(): List<HotspotSessionEntity> =
        sessions.filter { !it.synced && it.endTimestamp > 0 }

    override suspend fun markSynced(id: Long) {
        val index = sessions.indexOfFirst { it.id == id }
        if (index >= 0) {
            sessions[index] = sessions[index].copy(synced = true)
            changed()
        }
    }

    private fun changed() {
        revision.value += 1
    }
}
