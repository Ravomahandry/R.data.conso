package io.arvo.dataconso.ui.hotspot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.arvo.dataconso.R
import io.arvo.dataconso.domain.hotspot.HotspotAnomalyType
import io.arvo.dataconso.util.FormatUtils

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun HotspotInsightsScreen(
    onBack: () -> Unit,
    viewModel: HotspotHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hotspot_insights_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            CircularProgressIndicator(Modifier.padding(padding).padding(24.dp))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    InsightCard(stringResource(R.string.hotspot_forecast_title)) {
                        InsightLine(
                            stringResource(R.string.hotspot_quota_remaining),
                            FormatUtils.formatHotspotDataSize(state.forecast.remainingBytes)
                        )
                        InsightLine(
                            stringResource(R.string.hotspot_projected_usage),
                            FormatUtils.formatHotspotDataSize(state.forecast.projectedPeriodEndBytes)
                        )
                        InsightLine(
                            stringResource(R.string.hotspot_quota_exhaustion),
                            state.forecast.daysUntilQuotaExhausted?.let {
                                stringResource(R.string.hotspot_days_value, it)
                            } ?: stringResource(R.string.hotspot_forecast_unavailable)
                        )
                        Text(
                            stringResource(R.string.hotspot_estimate_note),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item {
                    InsightCard(stringResource(R.string.hotspot_health_title)) {
                        InsightLine(
                            stringResource(R.string.hotspot_health_score),
                            stringResource(R.string.hotspot_score_value, state.healthScore.score)
                        )
                        InsightLine(
                            stringResource(R.string.hotspot_average_duration),
                            formatDuration(state.analyticsReport.averageDurationMillis)
                        )
                        InsightLine(
                            stringResource(R.string.hotspot_cumulative_duration),
                            formatDuration(state.analyticsReport.totalDurationMillis)
                        )
                    }
                }
                item {
                    InsightCard(stringResource(R.string.hotspot_recommendations)) {
                        val daysToExhaustion = state.forecast.daysUntilQuotaExhausted
                        Text(
                            if (daysToExhaustion != null && daysToExhaustion <= 7) {
                                stringResource(R.string.hotspot_recommendation_quota_risk, daysToExhaustion)
                            } else if (state.forecast.averageDailyBytes > 0) {
                                stringResource(R.string.hotspot_recommendation_usage)
                            } else {
                                stringResource(R.string.hotspot_recommendation_insufficient_data)
                            }
                        )
                        if (state.anomalies.isNotEmpty()) {
                            Text(stringResource(R.string.hotspot_recommendation_review_sessions))
                        }
                    }
                }
                item {
                    InsightCard(stringResource(R.string.hotspot_top_sessions)) {
                        if (state.analyticsReport.topSessions.isEmpty()) {
                            Text(stringResource(R.string.hotspot_no_sessions))
                        }
                        InsightLine(
                            stringResource(R.string.hotspot_peak_consumption),
                            FormatUtils.formatHotspotDataSize(state.analyticsReport.peakSessionBytes)
                        )
                    }
                }
                items(state.analyticsReport.topDays, key = { it.first }) { (day, bytes) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                java.text.DateFormat.getDateInstance(java.text.DateFormat.MEDIUM)
                                    .format(java.util.Date(day)),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(FormatUtils.formatHotspotDataSize(bytes))
                        }
                    }
                }
                items(state.analyticsReport.topSessions, key = { "top-${it.id}" }) { session ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    ) {
                        Column(Modifier.padding(14.dp)) {
                            Text(
                                java.text.DateFormat.getDateTimeInstance(
                                    java.text.DateFormat.MEDIUM,
                                    java.text.DateFormat.SHORT
                                ).format(java.util.Date(session.startTimestamp)),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(FormatUtils.formatHotspotDataSize(session.totalBytes))
                        }
                    }
                }
                item {
                    InsightCard(stringResource(R.string.hotspot_anomalies)) {
                        if (state.anomalies.isEmpty()) {
                            Text(stringResource(R.string.hotspot_no_anomalies))
                        } else {
                            state.anomalies.take(10).forEach { anomaly ->
                                Text(
                                    "${anomaly.timestamp}: ${anomaly.message}",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                        if (state.anomalies.any { it.type == HotspotAnomalyType.NEW_DEVICE }) {
                            Text(stringResource(R.string.hotspot_new_device_detected))
                        }
                    }
                }
                item {
                    InsightCard(stringResource(R.string.hotspot_device_profiles)) {
                        if (state.deviceProfiles.isEmpty()) {
                            Text(stringResource(R.string.hotspot_devices_unavailable))
                        } else {
                            state.deviceProfiles.forEach { device ->
                                InsightLine(
                                    device.name,
                                    FormatUtils.formatHotspotDataSize(device.totalBytes)
                                )
                            }
                        }
                    }
                }
                item {
                    Text(
                        stringResource(R.string.hotspot_traffic_scope_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
private fun InsightCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content(this)
        }
    }
}

@Composable
private fun InsightLine(label: String, value: String) {
    androidx.compose.foundation.layout.Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.SemiBold)
    }
}
