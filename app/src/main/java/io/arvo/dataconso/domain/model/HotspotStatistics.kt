package io.arvo.dataconso.domain.model

import io.arvo.dataconso.data.HotspotSessionEntity

data class HotspotStatistics(
    val sessionCount: Int = 0,
    val totalBytes: Long = 0
)

data class HotspotAnalytics(
    val topDayBytes: Long = 0,
    val topWeekBytes: Long = 0,
    val topMonthBytes: Long = 0,
    val cumulativeDurationMillis: Long = 0,
    val cumulativeBytes: Long = 0,
    val sessionCount: Int = 0
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

    fun analytics(sessions: List<HotspotSessionEntity>, now: Long): HotspotAnalytics {
        val daily = mutableMapOf<Long, Long>()
        val weekly = mutableMapOf<Long, Long>()
        val monthly = mutableMapOf<Long, Long>()
        var duration = 0L
        var bytes = 0L

        sessions.forEach { session ->
            val sessionBytes = session.totalBytes.coerceAtLeast(0)
            val calendar = java.util.Calendar.getInstance().apply {
                timeInMillis = session.startTimestamp
            }
            addSaturated(daily, dayStart(calendar), sessionBytes)
            addSaturated(weekly, weekStart(calendar), sessionBytes)
            addSaturated(monthly, monthStart(calendar), sessionBytes)
            val end = if (session.endTimestamp > 0) session.endTimestamp else now
            val sessionDuration = durationMillis(session.startTimestamp, end)
            duration = saturatedAdd(duration, sessionDuration)
            bytes = saturatedAdd(bytes, sessionBytes)
        }

        return HotspotAnalytics(
            topDayBytes = daily.values.maxOrNull() ?: 0,
            topWeekBytes = weekly.values.maxOrNull() ?: 0,
            topMonthBytes = monthly.values.maxOrNull() ?: 0,
            cumulativeDurationMillis = duration,
            cumulativeBytes = bytes,
            sessionCount = sessions.size
        )
    }

    private fun dayStart(calendar: java.util.Calendar): Long =
        (calendar.clone() as java.util.Calendar).apply {
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun weekStart(calendar: java.util.Calendar): Long =
        (calendar.clone() as java.util.Calendar).apply {
            set(java.util.Calendar.DAY_OF_WEEK, firstDayOfWeek)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun monthStart(calendar: java.util.Calendar): Long =
        (calendar.clone() as java.util.Calendar).apply {
            set(java.util.Calendar.DAY_OF_MONTH, 1)
            set(java.util.Calendar.HOUR_OF_DAY, 0)
            set(java.util.Calendar.MINUTE, 0)
            set(java.util.Calendar.SECOND, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun addSaturated(values: MutableMap<Long, Long>, key: Long, bytes: Long) {
        values[key] = saturatedAdd(values[key] ?: 0, bytes)
    }

    private fun saturatedAdd(left: Long, right: Long): Long =
        if (Long.MAX_VALUE - left < right) Long.MAX_VALUE else left + right
}
