package io.arvo.dataconso

import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.model.HotspotStatisticsCalculator
import io.arvo.dataconso.repository.HotspotClock
import io.arvo.dataconso.repository.HotspotRepository
import io.arvo.dataconso.repository.HotspotTrafficCounters
import java.util.Calendar
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HotspotRepositoryTest {

    @Test
    fun sessionMeasuresTrafficDurationAndClosesHistory() = runBlocking {
        val dao = FakeHotspotSessionDao()
        val counters = MutableTrafficCounters(rx = 1_000, tx = 2_000)
        val clock = MutableHotspotClock(1_000_000)
        val repository = HotspotRepository(dao, counters, clock)

        val started = repository.startSession()
        counters.rx = 1_450
        counters.tx = 2_250
        clock.now += 90_000
        val measured = repository.refreshActiveSession()
        assertNotNull(measured)
        assertEquals(450L, measured?.rxBytes ?: -1)
        assertEquals(250L, measured?.txBytes ?: -1)
        assertEquals(700L, measured?.totalBytes ?: -1)
        assertEquals(90_000L, measured?.durationMillis ?: -1)

        counters.rx = 1_800
        counters.tx = 2_500
        clock.now += 30_000
        val ended = repository.stopSession()

        assertEquals(started.id, ended?.id)
        assertEquals(120_000L, ended?.durationMillis ?: -1)
        assertEquals(1_300L, ended?.totalBytes ?: -1)
        assertTrue((ended?.endTimestamp ?: 0) > 0)
        assertNull(repository.getCurrentSession().first())
        assertEquals(1, repository.getAllSessions().first().size)
    }

    @Test
    fun dailyAndMonthlyStatisticsOnlyIncludeSessionsInTheirPeriods() = runBlocking {
        val dao = FakeHotspotSessionDao()
        val today = Calendar.getInstance().apply {
            set(2025, Calendar.MAY, 15, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val repository = HotspotRepository(
            dao,
            MutableTrafficCounters(),
            MutableHotspotClock(today)
        )
        val todayStart = Calendar.getInstance().apply {
            timeInMillis = today
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val monthStart = Calendar.getInstance().apply {
            timeInMillis = today
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        dao.insert(session("today", todayStart + 1_000, totalBytes = 100))
        dao.insert(session("month", monthStart + 1_000, totalBytes = 200))
        dao.insert(session("old", monthStart - 1, totalBytes = 300))

        assertEquals(100L, repository.getTodayUsage().first())
        assertEquals(300L, repository.getMonthlyUsage().first())
        assertEquals(1, repository.getTodayStatistics().first().sessionCount)
        assertEquals(2, repository.getMonthlyStatistics().first().sessionCount)
        assertEquals(3, repository.getAllSessions().first().size)
    }

    @Test
    fun trafficCalculationsHandleInvalidCountersAndOverflow() {
        assertEquals(0, HotspotStatisticsCalculator.trafficDelta(-1, 100))
        assertEquals(0, HotspotStatisticsCalculator.trafficDelta(50, 100))
        assertEquals(25, HotspotStatisticsCalculator.trafficDelta(125, 100))
        assertEquals(Long.MAX_VALUE, HotspotStatisticsCalculator.totalBytes(Long.MAX_VALUE, 1))
        assertEquals(0, HotspotStatisticsCalculator.durationMillis(100, 50))
        assertFalse(session("closed", 1).isActive)
    }

    private fun session(
        id: String,
        timestamp: Long,
        totalBytes: Long = 0
    ) = HotspotSessionEntity(
        startTimestamp = timestamp,
        endTimestamp = timestamp + 1,
        durationMillis = 1,
        totalBytes = totalBytes,
        sessionId = id
    )

    private class MutableTrafficCounters(
        var rx: Long = 0,
        var tx: Long = 0
    ) : HotspotTrafficCounters {
        override fun rxBytes(): Long = rx
        override fun txBytes(): Long = tx
    }

    private class MutableHotspotClock(var now: Long) : HotspotClock {
        override fun nowMillis(): Long = now
    }
}
