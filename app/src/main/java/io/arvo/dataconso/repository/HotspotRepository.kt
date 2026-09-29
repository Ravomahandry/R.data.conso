package io.arvo.dataconso.repository

import android.net.TrafficStats
import io.arvo.dataconso.data.HotspotSessionDao
import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.model.HotspotStatistics
import io.arvo.dataconso.domain.model.HotspotStatisticsCalculator
import java.util.Calendar
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

interface HotspotTrafficCounters {
    fun rxBytes(): Long
    fun txBytes(): Long
}

interface HotspotClock {
    fun nowMillis(): Long
}

class DeviceHotspotTrafficCounters : HotspotTrafficCounters {
    override fun rxBytes(): Long = TrafficStats.getTotalRxBytes()
    override fun txBytes(): Long = TrafficStats.getTotalTxBytes()
}

class SystemHotspotClock : HotspotClock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}

class HotspotRepository @Inject constructor(
    private val sessionDao: HotspotSessionDao,
    private val trafficCounters: HotspotTrafficCounters,
    private val clock: HotspotClock
) {
    private val sessionMutex = Mutex()

    fun getCurrentSession(): Flow<HotspotSessionEntity?> =
        sessionDao.observeCurrentSession()

    fun getAllSessions(): Flow<List<HotspotSessionEntity>> =
        sessionDao.observeAllSessions()

    fun getTodayUsage(): Flow<Long> =
        sessionDao.observeSessionsFrom(startOfDay(clock.nowMillis()))
            .map { sessions -> HotspotStatisticsCalculator.summarize(sessions).totalBytes }

    fun getMonthlyUsage(): Flow<Long> =
        sessionDao.observeSessionsFrom(startOfMonth(clock.nowMillis()))
            .map { sessions -> HotspotStatisticsCalculator.summarize(sessions).totalBytes }

    fun getWeeklyStatistics(): Flow<HotspotStatistics> =
        sessionDao.observeSessionsFrom(startOfWeek(clock.nowMillis()))
            .map(HotspotStatisticsCalculator::summarize)

    fun getTodayStatistics(): Flow<HotspotStatistics> =
        sessionDao.observeSessionsFrom(startOfDay(clock.nowMillis()))
            .map(HotspotStatisticsCalculator::summarize)

    fun getMonthlyStatistics(): Flow<HotspotStatistics> =
        sessionDao.observeSessionsFrom(startOfMonth(clock.nowMillis()))
            .map(HotspotStatisticsCalculator::summarize)

    suspend fun startSession(): HotspotSessionEntity = sessionMutex.withLock {
        val now = clock.nowMillis()
        val current = sessionDao.getActiveSession()
        if (current != null) {
            return@withLock updateUsage(current, now)
        }

        val rxBaseline = trafficCounters.rxBytes()
        val txBaseline = trafficCounters.txBytes()
        val session = HotspotSessionEntity(
            startTimestamp = now,
            sessionId = UUID.randomUUID().toString(),
            baselineRxBytes = rxBaseline,
            baselineTxBytes = txBaseline,
            lastRxBytes = rxBaseline,
            lastTxBytes = txBaseline
        )
        val id = sessionDao.insert(session)
        check(id >= 0) { "Unable to persist hotspot session" }
        session.copy(id = id)
    }

    suspend fun stopSession(): HotspotSessionEntity? = sessionMutex.withLock {
        val current = sessionDao.getActiveSession() ?: return@withLock null
        val now = clock.nowMillis()
        val latest = updateUsage(current, now).copy(
            endTimestamp = now.coerceAtLeast(current.startTimestamp),
            durationMillis = HotspotStatisticsCalculator.durationMillis(
                current.startTimestamp,
                now.coerceAtLeast(current.startTimestamp)
            ),
            synced = false
        )
        sessionDao.update(latest)
        latest
    }

    suspend fun refreshActiveSession(): HotspotSessionEntity? = sessionMutex.withLock {
        val current = sessionDao.getActiveSession() ?: return@withLock null
        updateUsage(current, clock.nowMillis())
    }

    suspend fun getUnsyncedClosedSessions(): List<HotspotSessionEntity> =
        sessionDao.getUnsyncedClosedSessions()

    suspend fun markSynced(sessionId: Long, syncedAt: Long = clock.nowMillis()) {
        sessionDao.markSynced(sessionId, syncedAt)
    }

    suspend fun restoreSession(session: HotspotSessionEntity): Boolean {
        val existing = sessionDao.getBySyncKey(session.startTimestamp, session.sessionId)
        if (existing != null) return false
        val insertedId = sessionDao.insert(session.copy(id = 0, synced = true))
        return insertedId >= 0
    }

    private suspend fun updateUsage(
        session: HotspotSessionEntity,
        timestamp: Long
    ): HotspotSessionEntity {
        val sampledRx = trafficCounters.rxBytes()
        val sampledTx = trafficCounters.txBytes()
        val rx = accumulateBytes(session.rxBytes, session.lastRxBytes, sampledRx, session.baselineRxBytes)
        val tx = accumulateBytes(session.txBytes, session.lastTxBytes, sampledTx, session.baselineTxBytes)
        val updated = session.copy(
            durationMillis = HotspotStatisticsCalculator.durationMillis(
                session.startTimestamp,
                timestamp
            ),
            rxBytes = rx.total,
            txBytes = tx.total,
            totalBytes = HotspotStatisticsCalculator.totalBytes(rx.total, tx.total),
            lastRxBytes = rx.lastCounter,
            lastTxBytes = tx.lastCounter,
            synced = false
        )
        sessionDao.update(updated)
        return updated
    }

    private fun accumulateBytes(
        accumulated: Long,
        previousCounter: Long,
        currentCounter: Long,
        initialCounter: Long
    ): CounterAccumulation {
        if (currentCounter < 0) {
            return CounterAccumulation(accumulated, previousCounter)
        }
        val reference = if (previousCounter > 0) previousCounter else initialCounter
        val delta = HotspotStatisticsCalculator.trafficDelta(currentCounter, reference)
        val total = if (Long.MAX_VALUE - accumulated < delta) Long.MAX_VALUE else accumulated + delta
        return CounterAccumulation(total, currentCounter)
    }

    private data class CounterAccumulation(val total: Long, val lastCounter: Long)

    private fun startOfDay(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun startOfMonth(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun startOfWeek(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}
