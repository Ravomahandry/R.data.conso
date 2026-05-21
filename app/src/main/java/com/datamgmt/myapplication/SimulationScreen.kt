package com.datamgmt.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale

@Composable
fun SimulationScreen() {
    var quota by remember { mutableDoubleStateOf(30.0) }
    var currentUsage by remember { mutableDoubleStateOf(5.0) }
    var daysLeft by remember { mutableIntStateOf(15) }

    val dataRemaining = quota - currentUsage
    val suggestedDaily = if (daysLeft > 0) dataRemaining / daysLeft else dataRemaining
    val estimatedEnd = currentUsage + suggestedDaily * (daysLeft)
    val risk = if (estimatedEnd > quota) "Risque de dépassement" else "OK"

    Column(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Simulateur", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(value = quota.toString(), onValueChange = { quota = it.toDoubleOrNull() ?: quota }, label = { Text("Quota mensuel (Go)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = currentUsage.toString(), onValueChange = { currentUsage = it.toDoubleOrNull() ?: currentUsage }, label = { Text("Consommation actuelle (Go)") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(value = daysLeft.toString(), onValueChange = { daysLeft = it.toIntOrNull() ?: daysLeft }, label = { Text("Jours restants") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Quota journalier conseillé: ${String.format(Locale.getDefault(), "%.2f Go", suggestedDaily)}")
                Text("Estimation fin de mois: ${String.format(Locale.getDefault(), "%.2f Go", estimatedEnd)}")
                Text("Data restante: ${String.format(Locale.getDefault(), "%.2f Go", dataRemaining)}")
                Text("Statut: $risk")
            }
        }
    }
}
