package io.arvo.dataconso

import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.hotspot.HotspotAnalyticsEngine
import io.arvo.dataconso.domain.hotspot.HotspotAnomalyType
import io.arvo.dataconso.domain.hotspot.HotspotDeviceObservation
import java.util.Calendar
import org.junit.Assert.assertTrue
import org.junit.Test

class HotspotAnomalyTest {
    @Test
    fun detectsNightActivityHighConsumptionPeakAndNewDevice() {
        val now = Calendar.getInstance().apply {
            set(2025, Calendar.JANUARY, 10, 2, 0, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis
        val sessions = listOf(
            HotspotSessionEntity(
                startTimestamp = now - 3 * 86_400_000,
                endTimestamp = now - 3 * 86_400_000 + 1_000,
                totalBytes = 100,
                sessionId = "usual"
            ),
            HotspotSessionEntity(
                startTimestamp = now,
                totalBytes = 2_147_483_648L,
                sessionId = "peak"
            )
        )
        val observation = HotspotDeviceObservation("new-id", "New device", now, 50)

        val types = HotspotAnalyticsEngine.detectAnomalies(
            sessions,
            knownDeviceIds = emptySet(),
            observations = listOf(observation),
            now = now
        ).map { it.type }

        assertTrue(HotspotAnomalyType.NIGHT_ACTIVITY in types)
        assertTrue(HotspotAnomalyType.UNUSUAL_PEAK in types)
        assertTrue(HotspotAnomalyType.HIGH_CONSUMPTION in types)
        assertTrue(HotspotAnomalyType.NEW_DEVICE in types)
    }
}
