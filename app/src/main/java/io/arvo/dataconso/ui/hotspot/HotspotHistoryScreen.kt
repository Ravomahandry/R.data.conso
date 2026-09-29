package io.arvo.dataconso.ui.hotspot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.WifiTethering
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.arvo.dataconso.R
import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.util.FormatUtils
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotspotHistoryScreen(
    onBack: () -> Unit,
    viewModel: HotspotHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.hotspot_history_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.padding(padding).padding(24.dp)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    HotspotStatsCard(
                        title = stringResource(R.string.hotspot_today),
                        bytes = state.today.totalBytes,
                        sessions = state.today.sessionCount,
                        detail = null
                    )
                }
                item {
                    HotspotStatsCard(
                        title = stringResource(R.string.hotspot_week),
                        bytes = state.week.totalBytes,
                        sessions = state.week.sessionCount,
                        detail = null
                    )
                }
                item {
                    Text(
                        stringResource(R.string.hotspot_estimate_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                item {
                    HotspotStatsCard(
                        title = stringResource(R.string.hotspot_month),
                        bytes = state.month.totalBytes,
                        sessions = state.month.sessionCount,
                        detail = null
                    )
                }
                item { HotspotAnalyticsSection(state.analytics) }
                item {
                    Text(
                        text = stringResource(R.string.hotspot_sessions),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
                if (state.sessions.isEmpty()) {
                    item { Text(stringResource(R.string.hotspot_no_sessions)) }
                } else {
                    items(state.sessions, key = { it.id }) { session ->
                        HotspotSessionItem(session)
                    }
                }
                state.errorMessage?.let { message ->
                    item { Text(message, color = MaterialTheme.colorScheme.error) }
                }
            }
        }
    }
}

@Composable
private fun HotspotStatsCard(
    title: String,
    bytes: Long,
    sessions: Int,
    detail: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                FormatUtils.formatHotspotDataSize(bytes),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(stringResource(R.string.hotspot_session_count, sessions))
            detail?.let { Text(it, style = MaterialTheme.typography.bodySmall) }
        }
    }
}

@Composable
private fun HotspotSessionItem(
    session: HotspotSessionEntity
) {
    val endTime = if (session.endTimestamp == 0L) System.currentTimeMillis() else session.endTimestamp
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.WifiTethering, contentDescription = null)
                Text(
                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(session.startTimestamp)),
                    modifier = Modifier.padding(start = 8.dp),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.height(6.dp))
            DetailLine(
                stringResource(R.string.hotspot_start_time),
                DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(session.startTimestamp))
            )
            DetailLine(
                stringResource(R.string.hotspot_end_time),
                if (session.endTimestamp == 0L) stringResource(R.string.hotspot_status_active)
                else DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(session.endTimestamp))
            )
            DetailLine(
                stringResource(R.string.hotspot_duration_label),
                formatDuration(
                    session.durationMillis.coerceAtLeast(endTime - session.startTimestamp)
                )
            )
            DetailLine(stringResource(R.string.hotspot_rx), FormatUtils.formatHotspotDataSize(session.rxBytes))
            DetailLine(stringResource(R.string.hotspot_tx), FormatUtils.formatHotspotDataSize(session.txBytes))
            DetailLine(stringResource(R.string.hotspot_total), FormatUtils.formatHotspotDataSize(session.totalBytes))
            val duration = session.durationMillis.coerceAtLeast(endTime - session.startTimestamp)
            val averageBytesPerSecond = if (duration > 0) {
                (session.totalBytes.coerceAtLeast(0) * 1_000.0 / duration).toLong()
            } else 0L
            DetailLine(
                stringResource(R.string.hotspot_average_throughput),
                "${FormatUtils.formatHotspotDataSize(averageBytesPerSecond)}/s"
            )
            DetailLine(
                stringResource(R.string.hotspot_peak_throughput),
                stringResource(R.string.hotspot_measurement_unavailable)
            )
            DetailLine(
                stringResource(R.string.hotspot_connected_devices),
                stringResource(R.string.hotspot_devices_unavailable)
            )
            DetailLine(
                stringResource(R.string.hotspot_sync_status),
                if (session.synced) {
                    session.lastSyncedTimestamp.takeIf { it > 0 }?.let {
                        stringResource(
                            R.string.hotspot_synced_at,
                            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT).format(Date(it))
                        )
                    } ?: stringResource(R.string.hotspot_synced)
                } else stringResource(R.string.hotspot_not_synced)
            )
        }
    }
}

@Composable
private fun HotspotAnalyticsSection(analytics: io.arvo.dataconso.domain.model.HotspotAnalytics) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                stringResource(R.string.hotspot_analytics),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            DetailLine(stringResource(R.string.hotspot_top_day), FormatUtils.formatHotspotDataSize(analytics.topDayBytes))
            DetailLine(stringResource(R.string.hotspot_top_week), FormatUtils.formatHotspotDataSize(analytics.topWeekBytes))
            DetailLine(stringResource(R.string.hotspot_top_month), FormatUtils.formatHotspotDataSize(analytics.topMonthBytes))
            DetailLine(stringResource(R.string.hotspot_cumulative_duration), formatDuration(analytics.cumulativeDurationMillis))
            DetailLine(stringResource(R.string.hotspot_cumulative_consumption), FormatUtils.formatHotspotDataSize(analytics.cumulativeBytes))
            Text(
                stringResource(R.string.hotspot_session_count, analytics.sessionCount),
                modifier = Modifier.padding(top = 4.dp),
                fontWeight = FontWeight.Medium
            )
        }
    }
}
