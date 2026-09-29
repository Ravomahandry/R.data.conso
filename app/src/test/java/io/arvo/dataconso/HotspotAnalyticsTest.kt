package io.arvo.dataconso

import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.hotspot.HotspotAnalyticsEngine
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Test

class HotspotAnalyticsTest {
    @Test
    fun calculatesTotalsPeakAndAverageDuration() {
        val now = timestamp(2025, Calendar.MARCH, 12, 12)
        val sessions = listOf(
            session("one", now - 3_600_000, now - 1_800_000, 100),
            session("two", now - 2_000, now, 300)
        )

        val report = HotspotAnalyticsEngine.analyze(sessions, now)

        assertEquals(400L, report.todayBytes)
        assertEquals(300L, report.peakSessionBytes)
        assertEquals(1_802_000L, report.totalDurationMillis)
        assertEquals(901_000L, report.averageDurationMillis)
        assertEquals(2, report.sessionCount)
    }

    private fun session(id: String, start: Long, end: Long, bytes: Long) =
        HotspotSessionEntity(
            startTimestamp = start,
            endTimestamp = end,
            durationMillis = end - start,
            totalBytes = bytes,
            sessionId = id
        )

    private fun timestamp(year: Int, month: Int, day: Int, hour: Int): Long =
        Calendar.getInstance().apply {
            set(year, month, day, hour, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
}
