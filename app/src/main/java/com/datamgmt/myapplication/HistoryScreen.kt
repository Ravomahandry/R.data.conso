package com.datamgmt.myapplication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

data class HistoryRecord(val date: String, val sim: String, val bytes: Long)

@Composable
fun HistoryScreen(records: List<HistoryRecord> = emptyList()) {
    val list = remember { records }
    Column(modifier = Modifier.fillMaxSize().padding(12.dp)) {
        Text("Historique", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(list) { r ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), elevation = CardDefaults.cardElevation()) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(r.date)
                        Text(r.sim)
                        Text(DataUsageManager.humanReadable(r.bytes))
                    }
                }
            }
        }
    }
}
