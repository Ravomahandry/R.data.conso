package io.arvo.dataconso.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.arvo.dataconso.MainViewModel
import io.arvo.dataconso.domain.usecase.VpnDiagnosticEvent
import io.arvo.dataconso.domain.usecase.VpnDiagnosticEventType
import java.text.DateFormat
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VpnDiagnosticsScreen(
    uiState: MainViewModel.UiState,
    onBack: () -> Unit
) {
    val events = uiState.vpnDiagnosticEvents.sortedByDescending { it.timestamp }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnostics VPN") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Rounded.ArrowBack, contentDescription = "Retour")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                VpnHealthCard(
                    runtime = uiState.vpnRuntime,
                    onOpenDiagnostics = {},
                    showDetailsAction = false
                )
            }
            item { EventSectionTitle("Historique des erreurs") }
            items(events.filter { it.type == VpnDiagnosticEventType.ERROR }, key = { it.id }) {
                DiagnosticEventCard(it)
            }
            item { EventSectionTitle("Reconstructions du tunnel") }
            items(events.filter { it.type == VpnDiagnosticEventType.TUNNEL_REBUILD }, key = { it.id }) {
                DiagnosticEventCard(it)
            }
            item { EventSectionTitle("Historique des blocages") }
            items(
                events.filter {
                    it.type == VpnDiagnosticEventType.BLOCK_STARTED ||
                        it.type == VpnDiagnosticEventType.BLOCK_APPLIED ||
                        it.type == VpnDiagnosticEventType.BLOCK_RELEASED
                },
                key = { it.id }
            ) {
                DiagnosticEventCard(it)
            }
        }
    }
}

@Composable
private fun EventSectionTitle(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 8.dp)
    )
}

@Composable
private fun DiagnosticEventCard(event: VpnDiagnosticEvent) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM)
                    .format(Date(event.timestamp)),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(event.message, style = MaterialTheme.typography.bodyMedium)
            event.packageName?.let {
                Text(it, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
