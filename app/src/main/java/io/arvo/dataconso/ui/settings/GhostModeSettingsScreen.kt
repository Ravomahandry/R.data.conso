package io.arvo.dataconso.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.arvo.dataconso.DnsProvider
import io.arvo.dataconso.AppSettings
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GhostModeSettingsScreen(
    settings: AppSettings,
    currentProvider: DnsProvider,
    isGhostModeEnabled: Boolean,
    onProviderSelected: (DnsProvider) -> Unit,
    onToggleGhostMode: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ghost Mode & DNS") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                GhostModeStatusCard(isEnabled = isGhostModeEnabled, onToggle = onToggleGhostMode)
            }

            if (isGhostModeEnabled) {
                item {
                    GhostModeStatsRow(trackers = settings.blockedTrackersCount, dataSaved = settings.dataSavedMb)
                }

                item {
                    Text(
                        text = "Fournisseur DNS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                items(DnsProvider.values()) { provider ->
                    DnsProviderItem(
                        provider = provider,
                        isSelected = provider == currentProvider,
                        onSelect = { onProviderSelected(provider) }
                    )
                }
                
                item {
                    SecurityNote()
                }
            }
        }
    }
}

@Composable
fun GhostModeStatusCard(isEnabled: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(32.dp))
            Column(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                Text("Mode Fantôme", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    if (isEnabled) "Navigation privée et filtrée active" else "Désactivé (Recommandé pour économie batterie)",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(checked = isEnabled, onCheckedChange = onToggle)
        }
    }
}

@Composable
fun GhostModeStatsRow(trackers: Int, dataSaved: Double) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        StatCard(
            title = "Traqueurs Bloqués",
            value = trackers.toString(),
            icon = Icons.Default.BugReport,
            color = Color(0xFFF43F5E),
            modifier = Modifier.weight(1f)
        )
        StatCard(
            title = "Data Économisée",
            value = String.format(java.util.Locale.getDefault(), "%.1f Mo", dataSaved),
            icon = Icons.Default.DataUsage,
            color = Color(0xFF10B981),
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun StatCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
            Text(title, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun DnsProviderItem(provider: DnsProvider, isSelected: Boolean, onSelect: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onSelect() },
        border = if (isSelected) CardDefaults.outlinedCardBorder() else null,
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(provider.label, fontWeight = FontWeight.Bold)
                Text("${provider.primary} • ${provider.secondary}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (isSelected) {
                Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun SecurityNote() {
    Text(
        text = "Note : le Ghost Mode utilise un VPN local pour filtrer les requetes DNS. Certaines donnees de configuration et de synthese peuvent encore etre synchronisees via Firebase.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.padding(top = 16.dp)
    )
}
