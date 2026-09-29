package io.arvo.dataconso.domain.model

import io.arvo.dataconso.data.HotspotSessionEntity

data class HotspotStatistics(
    val sessionCount: Int = 0,
    val totalBytes: Long = 0
)

object HotspotStatisticsCalculator {
    fun totalBytes(rxBytes: Long, txBytes: Long): Long =
        rxBytes.coerceAtLeast(0).let { rx ->
            val tx = txBytes.coerceAtLeast(0)
            if (Long.MAX_VALUE - rx < tx) Long.MAX_VALUE else rx + tx
        }

    fun trafficDelta(currentBytes: Long, baselineBytes: Long): Long {
        if (currentBytes < 0 || baselineBytes < 0 || currentBytes < baselineBytes) return 0
        return currentBytes - baselineBytes
    }

    fun durationMillis(startTimestamp: Long, endTimestamp: Long): Long =
        (endTimestamp - startTimestamp).coerceAtLeast(0)

    fun summarize(sessions: List<HotspotSessionEntity>): HotspotStatistics =
        HotspotStatistics(
            sessionCount = sessions.size,
            totalBytes = sessions.fold(0L) { total, session ->
                val bytes = session.totalBytes.coerceAtLeast(0)
                if (Long.MAX_VALUE - total < bytes) Long.MAX_VALUE else total + bytes
            }
        )
}
