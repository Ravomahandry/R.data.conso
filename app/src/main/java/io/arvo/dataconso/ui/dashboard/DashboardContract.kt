package io.arvo.dataconso.ui.dashboard

import io.arvo.dataconso.AppUsageInfo
import io.arvo.dataconso.Granularity
import io.arvo.dataconso.HistoryEntry
import io.arvo.dataconso.NetworkSource
import io.arvo.dataconso.domain.model.UsageData

data class DashboardState(
    val isLoading: Boolean = false,
    val usageData: UsageData = UsageData(),
    val history: List<HistoryEntry> = emptyList(),
    val predictions: List<Double> = emptyList(),
    val topApps: List<AppUsageInfo> = emptyList(),
    val currentSource: NetworkSource = NetworkSource.MOBILE,
    val currentGranularity: Granularity = Granularity.DAILY,
    val errorMessage: String? = null
)

sealed class DashboardIntent {
    object Refresh : DashboardIntent()
    data class SelectSource(val source: NetworkSource) : DashboardIntent()
    data class SelectGranularity(val granularity: Granularity) : DashboardIntent()
}

sealed class DashboardEffect {
    data class ShowError(val message: String) : DashboardEffect()
}
