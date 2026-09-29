package io.arvo.dataconso.domain.hotspot

import io.arvo.dataconso.data.HotspotSessionEntity
import java.util.Calendar

data class HotspotUsageForecast(
    val quotaBytes: Long,
    val usedBytes: Long,
    val remainingBytes: Long,
    val averageDailyBytes: Long,
    val projectedPeriodEndBytes: Long,
    val daysUntilQuotaExhausted: Int?,
    val quotaExhausted: Boolean
)

data class HotspotAnalyticsReport(
    val todayBytes: Long,
    val weekBytes: Long,
    val monthBytes: Long,
    val peakSessionBytes: Long,
    val averageDurationMillis: Long,
    val totalDurationMillis: Long,
    val totalBytes: Long,
    val sessionCount: Int,
    val topSessions: List<HotspotSessionEntity>,
    val topDays: List<Pair<Long, Long>>
)

data class HotspotDeviceProfile(
    val deviceId: String,
    val name: String,
    val firstConnectionTimestamp: Long,
    val lastConnectionTimestamp: Long,
    val sessionCount: Int,
    val totalBytes: Long
)

data class HotspotDeviceObservation(
    val deviceId: String,
    val name: String,
    val timestamp: Long,
    val bytes: Long
)

enum class HotspotAnomalyType {
    HIGH_CONSUMPTION,
    NEW_DEVICE,
    NIGHT_ACTIVITY,
    UNUSUAL_PEAK
}

data class HotspotAnomaly(
    val type: HotspotAnomalyType,
    val timestamp: Long,
    val message: String
)

data class HotspotHealthScore(
    val score: Int,
    val durationScore: Int,
    val frequencyScore: Int,
    val batteryScore: Int,
    val consumptionScore: Int
)

object HotspotAnalyticsEngine {
    fun analyze(sessions: List<HotspotSessionEntity>, now: Long): HotspotAnalyticsReport {
        val dayStart = startOfDay(now)
        val weekStart = startOfWeek(now)
        val monthStart = startOfMonth(now)
        val today = sessions.filter { it.startTimestamp >= dayStart }
        val week = sessions.filter { it.startTimestamp >= weekStart }
        val month = sessions.filter { it.startTimestamp >= monthStart }
        val closedOrCurrentDurations = sessions.map {
            (if (it.endTimestamp > 0) it.endTimestamp else now)
                .minus(it.startTimestamp)
                .coerceAtLeast(0)
        }
        val totalDuration = closedOrCurrentDurations.fold(0L, ::saturatedAdd)
        val totalBytes = sessions.fold(0L) { total, session ->
            saturatedAdd(total, session.totalBytes.coerceAtLeast(0))
        }
        val dailyTotals = sessions.groupBy { startOfDay(it.startTimestamp) }
            .mapValues { (_, entries) ->
                entries.fold(0L) { total, session ->
                    saturatedAdd(total, session.totalBytes.coerceAtLeast(0))
                }
            }

        return HotspotAnalyticsReport(
            todayBytes = sumBytes(today),
            weekBytes = sumBytes(week),
            monthBytes = sumBytes(month),
            peakSessionBytes = sessions.maxOfOrNull { it.totalBytes.coerceAtLeast(0) } ?: 0,
            averageDurationMillis = if (sessions.isEmpty()) 0 else totalDuration / sessions.size,
            totalDurationMillis = totalDuration,
            totalBytes = totalBytes,
            sessionCount = sessions.size,
            topSessions = sessions.sortedByDescending { it.totalBytes }.take(5),
            topDays = dailyTotals.entries.sortedByDescending { it.value }.take(5)
                .map { it.key to it.value }
        )
    }

    fun forecast(
        sessions: List<HotspotSessionEntity>,
        quotaBytes: Long,
        now: Long,
        lookbackDays: Int = 7
    ): HotspotUsageForecast {
        val quota = quotaBytes.coerceAtLeast(0)
        val monthStart = startOfMonth(now)
        val used = sumBytes(sessions.filter { it.startTimestamp >= monthStart })
        val remaining = (quota - used).coerceAtLeast(0)
        val lookbackStart = now - lookbackDays.coerceAtLeast(1) * MILLIS_PER_DAY
        val recentBytes = sumBytes(sessions.filter { it.startTimestamp >= lookbackStart })
        val averageDaily = if (lookbackDays <= 0) 0 else recentBytes / lookbackDays
        val daysInPeriod = Calendar.getInstance().apply {
            timeInMillis = now
        }.getActualMaximum(Calendar.DAY_OF_MONTH)
        val daysRemainingInMonth = (daysInPeriod - Calendar.getInstance().apply {
            timeInMillis = now
        }.get(Calendar.DAY_OF_MONTH) + 1).coerceAtLeast(1)
        val projected = saturatedAdd(used, saturatedMultiply(averageDaily, daysRemainingInMonth.toLong()))
        val daysToQuota = when {
            quota == 0L -> null
            remaining == 0L -> 0
            averageDaily == 0L -> null
            else -> (remaining / averageDaily + if (remaining % averageDaily == 0L) 0 else 1)
                .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
        }

        return HotspotUsageForecast(
            quotaBytes = quota,
            usedBytes = used,
            remainingBytes = remaining,
            averageDailyBytes = averageDaily,
            projectedPeriodEndBytes = projected,
            daysUntilQuotaExhausted = daysToQuota,
            quotaExhausted = quota > 0 && used >= quota
        )
    }

    fun healthScore(
        sessions: List<HotspotSessionEntity>,
        batteryPercent: Int?,
        now: Long
    ): HotspotHealthScore {
        val report = analyze(sessions, now)
        val durationHours = report.averageDurationMillis / 3_600_000.0
        val durationScore = (100 - (durationHours * 8).toInt()).coerceIn(0, 100)
        val recentCount = sessions.count { it.startTimestamp >= now - 7 * MILLIS_PER_DAY }
        val frequencyScore = (100 - recentCount * 8).coerceIn(0, 100)
        val batteryScore = batteryPercent?.coerceIn(0, 100) ?: 50
        val dailyBytes = report.todayBytes.toDouble()
        val consumptionScore = when {
            dailyBytes >= 5L * GIB -> 10
            dailyBytes >= 2L * GIB -> 35
            dailyBytes >= GIB -> 60
            else -> 100
        }
        val overall = (
            durationScore * 0.20 +
                frequencyScore * 0.20 +
                batteryScore * 0.20 +
                consumptionScore * 0.40
            ).toInt().coerceIn(0, 100)
        return HotspotHealthScore(overall, durationScore, frequencyScore, batteryScore, consumptionScore)
    }

    fun detectAnomalies(
        sessions: List<HotspotSessionEntity>,
        knownDeviceIds: Set<String>,
        observations: List<HotspotDeviceObservation>,
        now: Long
    ): List<HotspotAnomaly> {
        val results = mutableListOf<HotspotAnomaly>()
        val historical = sessions.filter { it.startTimestamp < now - MILLIS_PER_DAY }
        val historicalDailyAverage = if (historical.isEmpty()) 0L else
            historical.fold(0L) { total, session ->
                saturatedAdd(total, session.totalBytes.coerceAtLeast(0))
            } /
                historical.map { startOfDay(it.startTimestamp) }.distinct().size.coerceAtLeast(1)

        sessions.forEach { session ->
            val time = if (session.endTimestamp > 0) session.endTimestamp else now
            if (historicalDailyAverage > 0 && session.totalBytes > historicalDailyAverage * 2) {
                results += HotspotAnomaly(HotspotAnomalyType.HIGH_CONSUMPTION, time, "Consommation supérieure à la moyenne historique")
            }
            if (session.totalBytes >= 2 * GIB) {
                results += HotspotAnomaly(HotspotAnomalyType.UNUSUAL_PEAK, time, "Pic de consommation inhabituel")
            }
            val calendar = Calendar.getInstance().apply { timeInMillis = session.startTimestamp }
            if (calendar.get(Calendar.HOUR_OF_DAY) in 0..5) {
                results += HotspotAnomaly(HotspotAnomalyType.NIGHT_ACTIVITY, session.startTimestamp, "Activité hotspot nocturne")
            }
        }
        observations.filterNot { it.deviceId in knownDeviceIds }.forEach {
            results += HotspotAnomaly(HotspotAnomalyType.NEW_DEVICE, it.timestamp, "Nouvel appareil détecté : ${it.name}")
        }
        return results.sortedByDescending { it.timestamp }
    }

    fun buildDeviceProfiles(observations: List<HotspotDeviceObservation>): List<HotspotDeviceProfile> =
        observations.groupBy { it.deviceId }.map { (id, entries) ->
            HotspotDeviceProfile(
                deviceId = id,
                name = entries.last().name,
                firstConnectionTimestamp = entries.minOf { it.timestamp },
                lastConnectionTimestamp = entries.maxOf { it.timestamp },
                sessionCount = entries.size,
                totalBytes = entries.fold(0L) { total, item ->
                    saturatedAdd(total, item.bytes.coerceAtLeast(0))
                }
            )
        }.sortedByDescending { it.totalBytes }

    private fun sumBytes(sessions: List<HotspotSessionEntity>): Long =
        sessions.fold(0L) { total, session ->
            saturatedAdd(total, session.totalBytes.coerceAtLeast(0))
        }

    private fun startOfDay(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
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

    private fun startOfMonth(timestamp: Long): Long =
        Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

    private fun saturatedAdd(left: Long, right: Long): Long =
        if (right > 0 && Long.MAX_VALUE - left < right) Long.MAX_VALUE else left + right

    private fun saturatedMultiply(left: Long, right: Long): Long =
        if (left > 0 && right > Long.MAX_VALUE / left) Long.MAX_VALUE else left * right

    private const val MILLIS_PER_DAY = 86_400_000L
    private const val GIB = 1_073_741_824L
}

object HotspotUsageForecastEngine {
    fun calculate(sessions: List<HotspotSessionEntity>, quotaBytes: Long, now: Long) =
        HotspotAnalyticsEngine.forecast(sessions, quotaBytes, now)
}

object HotspotHealthScoreEngine {
    fun calculate(sessions: List<HotspotSessionEntity>, batteryPercent: Int?, now: Long) =
        HotspotAnalyticsEngine.healthScore(sessions, batteryPercent, now)
}

object HotspotDeviceProfileEngine {
    fun build(observations: List<HotspotDeviceObservation>) =
        HotspotAnalyticsEngine.buildDeviceProfiles(observations)
}

object HotspotAnomalyDetector {
    fun detect(
        sessions: List<HotspotSessionEntity>,
        knownDeviceIds: Set<String>,
        observations: List<HotspotDeviceObservation>,
        now: Long
    ) = HotspotAnalyticsEngine.detectAnomalies(sessions, knownDeviceIds, observations, now)
}
