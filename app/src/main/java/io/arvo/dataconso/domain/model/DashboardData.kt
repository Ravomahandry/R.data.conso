package io.arvo.dataconso.domain.model

import io.arvo.dataconso.AppUsageInfo
import io.arvo.dataconso.HistoryEntry

data class DashboardData(
    val usage: UsageData = UsageData(),
    val history: List<HistoryEntry> = emptyList(),
    val topApps: List<AppUsageInfo> = emptyList()
)
