package io.arvo.dataconso

import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.hotspot.HotspotAnalyticsEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class HotspotHealthScoreTest {
    @Test
    fun combinesUsageAndBatteryWithinZeroToHundredRange() {
        val now = 1_000_000_000L
        val sessions = listOf(
            HotspotSessionEntity(
                startTimestamp = now - 60_000,
                endTimestamp = now,
                durationMillis = 60_000,
                totalBytes = 1_073_741_824L,
                sessionId = "session"
            )
        )

        val score = HotspotAnalyticsEngine.healthScore(sessions, 72, now)

        assertEquals(72, score.batteryScore)
        assertTrue(score.score in 0..100)
        assertTrue(score.consumptionScore in 0..100)
    }
}
