package io.arvo.dataconso

import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.model.HotspotStatisticsCalculator
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Test

class HotspotStatisticsTest {

    @Test
    fun calculatesTopDayWeekMonthAndCumulativeTotals() {
        val calendar = Calendar.getInstance().apply {
            set(2025, Calendar.JANUARY, 6, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val monday = calendar.timeInMillis
        val tuesday = (calendar.clone() as Calendar).apply {
            add(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis
        val nextMonth = (calendar.clone() as Calendar).apply {
            add(Calendar.MONTH, 1)
        }.timeInMillis

        val sessions = listOf(
            session("a", monday, 100, 1_000),
            session("b", monday + 60_000, 300, 2_000),
            session("c", tuesday, 250, 3_000),
            session("d", nextMonth, 50, 4_000)
        )

        val result = HotspotStatisticsCalculator.analytics(sessions, nextMonth + 10_000)

        assertEquals(400L, result.topDayBytes)
        assertEquals(650L, result.topWeekBytes)
        assertEquals(650L, result.topMonthBytes)
        assertEquals(10_000L, result.cumulativeDurationMillis)
        assertEquals(700L, result.cumulativeBytes)
        assertEquals(4, result.sessionCount)
    }

    @Test
    fun includesLiveSessionDurationAndSaturatesOnOverflow() {
        val liveStart = 10_000L
        val live = HotspotSessionEntity(
            startTimestamp = liveStart,
            sessionId = "live",
            totalBytes = Long.MAX_VALUE
        )

        val result = HotspotStatisticsCalculator.analytics(listOf(live), liveStart + 5_000)

        assertEquals(5_000L, result.cumulativeDurationMillis)
        assertEquals(Long.MAX_VALUE, result.cumulativeBytes)
        assertEquals(Long.MAX_VALUE, result.topDayBytes)
    }

    private fun session(id: String, start: Long, bytes: Long, duration: Long) =
        HotspotSessionEntity(
            startTimestamp = start,
            endTimestamp = start + duration,
            durationMillis = duration,
            totalBytes = bytes,
            sessionId = id
        )
}
