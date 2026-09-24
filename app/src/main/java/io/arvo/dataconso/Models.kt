package io.arvo.dataconso

import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable

enum class NetworkSource(val labelRes: Int, val id: String) {
    MOBILE(R.string.source_mobile, "SIM_COMBINED"),
    WIFI(R.string.source_wifi, "WIFI"),
    TOTAL(R.string.source_total, "TOTAL")
}

enum class Granularity(val labelRes: Int) {
    DAILY(R.string.gran_daily),
    WEEKLY(R.string.gran_weekly),
    MONTHLY(R.string.gran_monthly)
}

enum class AppTheme(val label: String) {
    LIGHT("ARVO Clair"),
    DARK("ARVO Sombre"),
    OCEAN("ARVO Elite")
}

enum class DnsProvider(val label: String, val primary: String, val secondary: String) {
    ADGUARD("AdGuard (Anti-Pub)", "94.140.14.14", "94.140.15.15"),
    CLOUDFLARE("Cloudflare (Rapide)", "1.1.1.1", "1.0.0.1"),
    GOOGLE("Google DNS", "8.8.8.8", "8.8.4.4"),
    QUAD9("Quad9 (Securise)", "9.9.9.9", "149.112.112.112")
}

@Immutable
data class AppUsageInfo(
    val packageName: String,
    val appName: String,
    val bytes: Long
)

@Immutable
data class AppQuota(
    val packageName: String,
    val appName: String,
    val quotaBytes: Long,
    val usedBytes: Long = 0L,
    val isBlocked: Boolean = false,
    val lastResetTime: Long = System.currentTimeMillis()
)

@Immutable
data class ArvoInsight(
    val title: String,
    val description: String,
    val type: InsightType,
    val priority: Int = 0,
    val actionId: String? = null
)

enum class InsightType {
    WARNING, SUGGESTION, ECONOMY, SECURITY
}

@Immutable
data class ProjectionResult(
    val recommendedDailyGb: Double,
    val daysRemaining: Int,
    @StringRes val statusRes: Int,
    val remainingGb: Double
)

@Immutable
data class SubscriptionInfoWrapper(
    val id: String,
    val displayNameRes: Int,
    val subId: Int
)
