package io.arvo.dataconso.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.arvo.dataconso.domain.usecase.VpnHealthCalculator
import io.arvo.dataconso.domain.usecase.VpnRuntimeSnapshot
import io.arvo.dataconso.domain.usecase.VpnRuntimeState
import java.text.DateFormat
import java.util.Date

@Composable
fun VpnHealthCard(
    runtime: VpnRuntimeSnapshot,
    onOpenDiagnostics: () -> Unit,
    showDetailsAction: Boolean = true,
    modifier: Modifier = Modifier
) {
    val uptime = runtime.tunnelSinceTimestamp?.let {
        (System.currentTimeMillis() - it).coerceAtLeast(0L)
    } ?: 0L
    val score = VpnHealthCalculator.calculate(
        tunnelActive = runtime.tunnelActive,
        ruleCount = runtime.monitoredApps,
        errorCount = runtime.errorCount,
        rebuildCount = runtime.rebuildCount,
        tunnelUptimeMillis = uptime
    )
    val stateColor = when (runtime.state) {
        VpnRuntimeState.ACTIVE -> Color(0xFF15803D)
        VpnRuntimeState.REBUILDING, VpnRuntimeState.PENDING -> Color(0xFFD97706)
        VpnRuntimeState.ERROR -> Color(0xFFDC2626)
        VpnRuntimeState.DISABLED -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val stateLabel = when (runtime.state) {
        VpnRuntimeState.ACTIVE -> "ACTIVE"
        VpnRuntimeState.REBUILDING -> "REBUILDING"
        VpnRuntimeState.ERROR -> "ERROR"
        VpnRuntimeState.PENDING -> "PENDING"
        VpnRuntimeState.DISABLED -> "DISABLED"
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Rounded.Security,
                    contentDescription = null,
                    tint = stateColor
                )
                Text(
                    text = "VPN • $stateLabel",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp).weight(1f)
                )
                Text("$score/100", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            }
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier.fillMaxWidth(),
                color = stateColor
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Tunnel: ${if (runtime.tunnelActive) "actif" else "inactif"}")
                Text("Apps surveillées: ${runtime.monitoredApps}")
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Apps bloquées: ${runtime.blockedApps}")
                Text(
                    text = runtime.lastSyncTimestamp?.let {
                        "Sync: ${DateFormat.getTimeInstance(DateFormat.SHORT).format(Date(it))}"
                    } ?: "Sync: --"
                )
            }
            runtime.errorMessage?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            if (showDetailsAction) {
                TextButton(onClick = onOpenDiagnostics, modifier = Modifier.align(Alignment.End)) {
                    Text("Ouvrir les diagnostics")
                }
            }
        }
    }
}
