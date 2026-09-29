package io.arvo.dataconso.ui.hotspot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.domain.hotspot.HotspotAnalyticsEngine
import io.arvo.dataconso.domain.hotspot.HotspotAnalyticsReport
import io.arvo.dataconso.domain.hotspot.HotspotAnomalyDetector
import io.arvo.dataconso.domain.hotspot.HotspotHealthScoreEngine
import io.arvo.dataconso.domain.hotspot.HotspotUsageForecastEngine
import io.arvo.dataconso.domain.hotspot.HotspotAnomaly
import io.arvo.dataconso.domain.hotspot.HotspotDeviceProfile
import io.arvo.dataconso.domain.hotspot.HotspotHealthScore
import io.arvo.dataconso.domain.hotspot.HotspotUsageForecast
import io.arvo.dataconso.domain.model.HotspotAnalytics
import io.arvo.dataconso.domain.model.HotspotStatistics
import io.arvo.dataconso.domain.model.HotspotStatisticsCalculator
import io.arvo.dataconso.repository.HotspotBatteryProvider
import io.arvo.dataconso.repository.HotspotQuotaProvider
import io.arvo.dataconso.repository.HotspotRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

data class HotspotHistoryState(
    val isLoading: Boolean = true,
    val isHotspotActive: Boolean = false,
    val currentSession: HotspotSessionEntity? = null,
    val sessions: List<HotspotSessionEntity> = emptyList(),
    val today: HotspotStatistics = HotspotStatistics(),
    val week: HotspotStatistics = HotspotStatistics(),
    val month: HotspotStatistics = HotspotStatistics(),
    val analytics: HotspotAnalytics = HotspotAnalytics(),
    val analyticsReport: HotspotAnalyticsReport = HotspotAnalyticsEngine.analyze(emptyList(), 0),
    val forecast: HotspotUsageForecast = HotspotAnalyticsEngine.forecast(emptyList(), 0, 0),
    val healthScore: HotspotHealthScore = HotspotAnalyticsEngine.healthScore(emptyList(), null, 0),
    val anomalies: List<HotspotAnomaly> = emptyList(),
    val deviceProfiles: List<HotspotDeviceProfile> = emptyList(),
    val connectedDeviceCount: Int? = null,
    val lastSyncTimestamp: Long? = null,
    val errorMessage: String? = null
)

@HiltViewModel
class HotspotHistoryViewModel @Inject constructor(
    private val hotspotRepository: HotspotRepository,
    private val quotaProvider: HotspotQuotaProvider,
    private val batteryProvider: HotspotBatteryProvider
) : ViewModel() {
    private val _uiState = MutableStateFlow(HotspotHistoryState())
    val uiState: StateFlow<HotspotHistoryState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val liveAndSessions = combine(
                hotspotRepository.getCurrentSession(),
                hotspotRepository.getAllSessions()
            ) { current, sessions -> current to sessions }
            val periodStatistics = combine(
                hotspotRepository.getTodayStatistics(),
                hotspotRepository.getWeeklyStatistics(),
                hotspotRepository.getMonthlyStatistics(),
                quotaProvider.monthlyQuotaBytes()
            ) { today, week, month, quotaBytes ->
                listOf(today, week, month) to quotaBytes
            }
            combine(liveAndSessions, periodStatistics) { (current, sessions), (periods, quotaBytes) ->
                val now = System.currentTimeMillis()
                val report = HotspotAnalyticsEngine.analyze(sessions, now)
                val battery = batteryProvider.batteryPercent()
                HotspotHistoryState(
                    isLoading = false,
                    isHotspotActive = current != null,
                    currentSession = current,
                    sessions = sessions,
                    today = periods[0],
                    week = periods[1],
                    month = periods[2],
                    analytics = HotspotStatisticsCalculator.analytics(sessions, now),
                    analyticsReport = report,
                    forecast = HotspotUsageForecastEngine.calculate(
                        sessions,
                        quotaBytes,
                        now
                    ),
                    healthScore = HotspotHealthScoreEngine.calculate(sessions, battery, now),
                    anomalies = HotspotAnomalyDetector.detect(
                        sessions,
                        knownDeviceIds = emptySet(),
                        observations = emptyList(),
                        now = now
                    ),
                    lastSyncTimestamp = sessions
                        .filter { it.lastSyncedTimestamp > 0 }
                        .maxOfOrNull { it.lastSyncedTimestamp }
                )
            }
                .catch { exception ->
                    _uiState.value = HotspotHistoryState(
                        isLoading = false,
                        errorMessage = exception.message
                    )
                }
                .collect { _uiState.value = it }
        }
    }
}
