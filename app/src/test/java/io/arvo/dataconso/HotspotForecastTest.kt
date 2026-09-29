package io.arvo.dataconso

import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.hotspot.HotspotAnalyticsEngine
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HotspotForecastTest {
    @Test
    fun forecastsRemainingQuotaAndExhaustion() {
        val now = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 10, 12, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val sessions = listOf(
            HotspotSessionEntity(
                startTimestamp = now - 86_400_000,
                endTimestamp = now - 80_000_000,
                totalBytes = 400,
                sessionId = "session"
            )
        )

        val forecast = HotspotAnalyticsEngine.forecast(sessions, 1_000, now)
        assertEquals(600L, forecast.remainingBytes)
        assertEquals(400L / 7, forecast.averageDailyBytes)
        assertTrue(forecast.projectedPeriodEndBytes >= forecast.usedBytes)

        val exhausted = HotspotAnalyticsEngine.forecast(sessions, 300, now)
        assertTrue(exhausted.quotaExhausted)
        assertEquals(0L, exhausted.remainingBytes)
        assertEquals(0, exhausted.daysUntilQuotaExhausted)
    }
}
