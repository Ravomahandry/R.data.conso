package io.arvo.dataconso.ui.hotspot

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
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
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
fun HotspotDashboardScreen(
    onOpenHistory: () -> Unit,
    onOpenInsights: () -> Unit,
    viewModel: HotspotHistoryViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.isLoading) {
        CircularProgressIndicator(modifier = Modifier.padding(24.dp))
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            start = 20.dp,
            top = 16.dp,
            end = 20.dp,
            bottom = 24.dp
        ),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            HotspotSectionTitle(stringResource(R.string.hotspot_current_state))
            Card(
                modifier = Modifier.fillMaxWidth().animateContentSize(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Rounded.WifiTethering,
                            contentDescription = null,
                            tint = if (state.isHotspotActive) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            stringResource(
                                if (state.isHotspotActive) R.string.hotspot_status_active
                                else R.string.hotspot_status_inactive
                            ),
                            modifier = Modifier.padding(start = 10.dp),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    AnimatedVisibility(visible = state.isHotspotActive) {
                        Column(modifier = Modifier.padding(top = 12.dp)) {
                            val current = state.currentSession
                            DetailLine(
                                stringResource(R.string.hotspot_start_time),
                                current?.startTimestamp?.let(::formatDateTime)
                                    ?: stringResource(R.string.hotspot_unknown)
                            )
                            DetailLine(
                                stringResource(R.string.hotspot_duration_label),
                                current?.let {
                                    formatDuration(
                                        (System.currentTimeMillis() - it.startTimestamp)
                                            .coerceAtLeast(it.durationMillis)
                                    )
                                } ?: "—"
                            )
                            DetailLine(
                                stringResource(R.string.hotspot_session_consumption),
                                current?.let { FormatUtils.formatHotspotDataSize(it.totalBytes) } ?: "0 B"
                            )
                        }
                    }
                    DetailLine(
                        stringResource(R.string.hotspot_connected_devices),
                        state.connectedDeviceCount?.toString()
                            ?: stringResource(R.string.hotspot_devices_unavailable)
                    )
                    DetailLine(
                        stringResource(R.string.hotspot_last_sync),
                        state.lastSyncTimestamp?.let(::formatDateTime)
                            ?: stringResource(R.string.hotspot_never_synced)
                    )
                }
            }
        }
        item {
            HotspotSectionTitle(stringResource(R.string.hotspot_consumption_title))
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                UsageCard(stringResource(R.string.hotspot_today), state.today.totalBytes)
                UsageCard(stringResource(R.string.hotspot_week), state.week.totalBytes)
                UsageCard(stringResource(R.string.hotspot_month), state.month.totalBytes)
            }
        }
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                onClick = onOpenInsights
            ) {
                Column(Modifier.padding(16.dp)) {
                    HotspotSectionTitle(stringResource(R.string.hotspot_forecast_title))
                    DetailLine(
                        stringResource(R.string.hotspot_quota_remaining),
                        FormatUtils.formatHotspotDataSize(state.forecast.remainingBytes)
                    )
                    DetailLine(
                        stringResource(R.string.hotspot_projected_usage),
                        FormatUtils.formatHotspotDataSize(state.forecast.projectedPeriodEndBytes)
                    )
                    DetailLine(
                        stringResource(R.string.hotspot_health_score),
                        stringResource(R.string.hotspot_score_value, state.healthScore.score)
                    )
                    Text(
                        stringResource(R.string.hotspot_open_insights),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HotspotSectionTitle(
                    stringResource(R.string.hotspot_recent_history),
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onOpenHistory) {
                    Icon(Icons.Rounded.History, contentDescription = null)
                    Text(stringResource(R.string.hotspot_view_all))
                }
            }
        }
        if (state.sessions.isEmpty()) {
            item { Text(stringResource(R.string.hotspot_no_sessions)) }
        } else {
            items(state.sessions.take(5), key = { it.id }) { session ->
                RecentSessionCard(session)
            }
        }
        item {
            Text(
                stringResource(R.string.hotspot_estimate_note),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun UsageCard(title: String, bytes: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(
                FormatUtils.formatHotspotDataSize(bytes),
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun RecentSessionCard(session: HotspotSessionEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(formatDateTime(session.startTimestamp), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                stringResource(
                    R.string.hotspot_rx_tx_total,
                    FormatUtils.formatHotspotDataSize(session.rxBytes),
                    FormatUtils.formatHotspotDataSize(session.txBytes),
                    FormatUtils.formatHotspotDataSize(session.totalBytes)
                ),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
internal fun HotspotSectionTitle(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        title,
        modifier = modifier.padding(vertical = 4.dp),
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold
    )
}

@Composable
internal fun DetailLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}

internal fun formatDuration(durationMillis: Long): String {
    val minutes = durationMillis.coerceAtLeast(0) / 60_000
    val days = minutes / (24 * 60)
    val hours = (minutes / 60) % 24
    val remainingMinutes = minutes % 60
    return when {
        days > 0 -> "${days}j ${hours}h ${remainingMinutes}min"
        hours > 0 -> "${hours}h ${remainingMinutes}min"
        else -> "${remainingMinutes}min"
    }
}

private fun formatDateTime(timestamp: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT).format(Date(timestamp))
