package io.arvo.dataconso

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.material.icons.rounded.History
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.NetworkCell
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.Analytics
import io.arvo.dataconso.ui.DataConsColors
import io.arvo.dataconso.ui.GlassCardSommite
import io.arvo.dataconso.ui.getDataConsColors
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.TextButton
import androidx.compose.material.icons.rounded.CalendarMonth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import androidx.compose.foundation.Image
import androidx.compose.ui.draw.clip
import com.google.accompanist.drawablepainter.rememberDrawablePainter

data class HistoryRecord(val date: String, val sim: String, val bytes: Long)

private data class HistoryStats(
    val avgUsage: Long,
    val maxUsage: Long,
    val peakRecord: HistoryRecord?,
    val totalUsage: Long
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    records: List<HistoryRecord> = emptyList(),
    selectedSource: NetworkSource = NetworkSource.TOTAL,
    colors: DataConsColors,
    isPremium: Boolean = true,
    onExportPdf: () -> Unit = {}
) {
    val context = LocalContext.current
    val formatter = remember { DataUsageManager(context) }
    
    // Sommité : Filtrage de l'historique avec déduplication Multi-SIM (Rigueur 5.0)
    val filteredRecords = remember(records, selectedSource) {
        records.groupBy { it.date }.map { (date, dayList) ->
            val wifi = dayList.find { it.sim == "WIFI" }?.bytes ?: 0L
            val combinedMobile = dayList.find { it.sim == "SIM_COMBINED" }
            val mobile = if (combinedMobile != null) {
                combinedMobile.bytes
            } else {
                dayList.filter { it.sim == "SIM_1" || it.sim == "SIM_2" }.sumOf { it.bytes }
            }
            
            when (selectedSource) {
                NetworkSource.WIFI -> HistoryRecord(date, "WIFI", wifi)
                NetworkSource.MOBILE -> HistoryRecord(date, "MOBILE", mobile)
                NetworkSource.TOTAL -> HistoryRecord(date, "TOTAL", wifi + mobile)
            }
        }.filter { it.bytes > 0 }.sortedByDescending { it.date }
    }
    
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp)) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(52.dp), shape = CircleShape, color = colors.primary.copy(alpha = 0.1f)) {
                    Icon(Icons.Rounded.History, null, tint = colors.primary, modifier = Modifier.padding(14.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(stringResource(R.string.tab_history), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                    Text(stringResource(selectedSource.labelRes).uppercase(), style = MaterialTheme.typography.labelSmall, color = colors.primary, letterSpacing = 1.sp)
                }
            }
            
            if (isPremium && filteredRecords.isNotEmpty()) {
                TextButton(onClick = onExportPdf) {
                    Icon(Icons.Rounded.Analytics, null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(stringResource(R.string.export_pdf_button), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // --- Date Picker for Daily Details --- 
        val showDatePicker = remember { mutableStateOf(false) }
        val selectedDateMillis = remember { mutableStateOf(System.currentTimeMillis()) }

        val formattedDate = remember(selectedDateMillis.value) {
            val date = Date(selectedDateMillis.value)
            SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(date)
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.history_date_selected, formattedDate),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colors.onSurface.copy(alpha = 0.8f)
            )
            OutlinedButton(onClick = { showDatePicker.value = true }) {
                Icon(Icons.Rounded.CalendarMonth, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text(stringResource(R.string.select_date))
            }
        }

        if (showDatePicker.value) {
            val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis.value)
            DatePickerDialog(
                onDismissRequest = { showDatePicker.value = false },
                confirmButton = {
                    TextButton(onClick = { 
                        selectedDateMillis.value = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
                        showDatePicker.value = false 
                    }) {
                        Text(stringResource(R.string.confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker.value = false }) {
                        Text(stringResource(R.string.cancel))
                    }
                }
            ) {
                DatePicker(state = datePickerState)
            }
        }

        // --- Daily App Usage for Selected Date (NEW SECTION) ---
        DailyAppUsageDetails(
            selectedDateMillis = selectedDateMillis.value,
            selectedSource = selectedSource,
            colors = colors
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        if (filteredRecords.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(stringResource(R.string.history_empty), color = colors.onSurface.copy(alpha = 0.5f))
            }
        } else {
            // Sommité : Déduplication des totaux globaux (Rigueur 4.5)
            val dailySummary = remember(records) {
                records.groupBy { it.date }.map { (date, list) ->
                    val wifi = list.filter { it.sim == "WIFI" }.sumOf { it.bytes }
                    val sims = list.filter { it.sim == "SIM_1" || it.sim == "SIM_2" }
                    val mobile = if (sims.isNotEmpty()) sims.sumOf { it.bytes } else list.filter { it.sim == "SIM_COMBINED" }.sumOf { it.bytes }
                    Triple(date, wifi, mobile)
                }
            }
            
            val wifiTotal = remember(dailySummary) { dailySummary.sumOf { it.second } }
            val mobileTotal = remember(dailySummary) { dailySummary.sumOf { it.third } }

            val stats = remember(filteredRecords) {
                var totalUsage = 0L
                var maxUsage = 0L
                var peakRec: HistoryRecord? = null
                
                filteredRecords.forEach {
                    totalUsage += it.bytes
                    if (it.bytes >= maxUsage) {
                        maxUsage = it.bytes
                        peakRec = it
                    }
                }
                
                val avg = if (filteredRecords.isNotEmpty()) totalUsage / filteredRecords.size else 0L
                HistoryStats(avg, maxUsage, peakRec, totalUsage)
            }
            val avgUsage = stats.avgUsage
            val maxUsage = stats.maxUsage
            val peakRecord = stats.peakRecord
            val totalUsage = stats.totalUsage


            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatMiniTile("MOYENNE", formatter.formatData(avgUsage), colors, Modifier.weight(1f))
                StatMiniTile("MAXIMUM", formatter.formatData(maxUsage), Color.Red, Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            // Sommité : Cartes d'insights simplifiées pour l'utilisateur
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassCardSommite(colors = colors, modifier = Modifier.weight(1f).height(100.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                        Text(stringResource(R.string.source_total).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.onSurface.copy(alpha = 0.6f))
                        Text(formatter.formatData(totalUsage), fontWeight = FontWeight.Black, fontSize = 18.sp, color = colors.primary)
                    }
                }
                GlassCardSommite(colors = colors, modifier = Modifier.weight(1f).height(100.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                        Text(stringResource(R.string.tab_simulation).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.onSurface.copy(alpha = 0.6f))
                        val projection = avgUsage * 30
                        Text(formatter.formatData(projection), fontWeight = FontWeight.Black, fontSize = 18.sp, color = colors.primary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GlassCardSommite(colors = colors, modifier = Modifier.weight(1f).height(100.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                        Text(stringResource(R.string.history_trend).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.onSurface.copy(alpha = 0.6f))
                        val trend = if (filteredRecords.size >= 2) {
                            val last = filteredRecords[0].bytes
                            val prev = filteredRecords[1].bytes
                            if (last > prev) stringResource(R.string.history_trend_up) else stringResource(R.string.history_trend_down)
                        } else stringResource(R.string.history_trend_stable)
                        val trendColor = if (trend == stringResource(R.string.history_trend_up)) Color.Red else Color(0xFF10B981)
                        Text(trend, fontWeight = FontWeight.Black, fontSize = 18.sp, color = trendColor)
                    }
                }
                GlassCardSommite(colors = colors, modifier = Modifier.weight(1f).height(100.dp)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
                        Text(stringResource(R.string.history_stability).uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.onSurface.copy(alpha = 0.6f))
                        Text("Stable", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Color(0xFF10B981))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            GlassCardSommite(colors = colors) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(stringResource(R.string.history_global_dist), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = colors.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(stringResource(R.string.wifi_label), fontSize = 12.sp, color = colors.onSurface.copy(alpha = 0.6f))
                            Text(formatter.formatData(wifiTotal), fontWeight = FontWeight.Black, fontSize = 20.sp, color = Color(0xFF10B981))
                        }
                        Box(modifier = Modifier.width(1.dp).height(40.dp).background(colors.outline.copy(alpha = 0.2f)))
                        Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                            Text(stringResource(R.string.mobile_label), fontSize = 12.sp, color = colors.onSurface.copy(alpha = 0.6f))
                            Text(formatter.formatData(mobileTotal), fontWeight = FontWeight.Black, fontSize = 20.sp, color = colors.primary)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(stringResource(R.string.history_recent_days), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = colors.onSurface.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(12.dp))

            val visibleRecords = if (isPremium) filteredRecords else filteredRecords.take(7)
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(visibleRecords) { r ->
                    val isWifi = r.sim == "WIFI"
                    val isTotal = r.sim == "TOTAL"
                    GlassCardSommite(colors = colors) {
                        Row(
                            modifier = Modifier.padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .background(
                                        if (isWifi) Color(0xFF10B981).copy(alpha = 0.1f) 
                                        else if (isTotal) colors.primary.copy(alpha = 0.2f)
                                        else colors.primary.copy(alpha = 0.1f),
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isWifi) Icons.Rounded.Wifi else if (isTotal) Icons.Rounded.History else Icons.Rounded.NetworkCell,
                                    contentDescription = null,
                                    tint = if (isWifi) Color(0xFF10B981) else colors.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Column(modifier = Modifier.weight(1f)) {
                                Text(r.date, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp)
                                Text(
                                    if (isWifi) stringResource(R.string.source_wifi) else if (isTotal) stringResource(R.string.source_total) else stringResource(R.string.source_mobile),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = colors.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    formatter.formatData(r.bytes),
                                    fontWeight = FontWeight.Black,
                                    color = if (r.bytes > 1024*1024*1000) Color.Red else colors.onSurface,
                                    fontSize = 18.sp
                                )
                                val isHigh = r.bytes > avgUsage * 1.2
                                Text(
                                    if (isHigh) stringResource(R.string.history_usage_high) else stringResource(R.string.history_usage_normal),
                                    fontSize = 10.sp,
                                    color = if (isHigh) Color.Red.copy(alpha = 0.8f) else Color(0xFF10B981).copy(alpha = 0.8f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                
                if (!isPremium && filteredRecords.size > 7) {
                    item { PremiumLockedHistoryCard(colors) }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.history_peak_analysis), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = Color.Red.copy(alpha = 0.7f))
                    Spacer(modifier = Modifier.height(12.dp))
                    GlassCardSommite(colors = colors) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            if (peakRecord != null) {
                                val label = if (peakRecord!!.sim == "TOTAL") stringResource(R.string.source_total) else if (peakRecord!!.sim == "WIFI") stringResource(R.string.source_wifi) else stringResource(R.string.source_mobile)
                                Text(
                                    stringResource(R.string.history_peak_msg, peakRecord!!.date, formatter.formatData(peakRecord!!.bytes), label),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = colors.onSurface.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatMiniTile(label: String, value: String, color: Any, modifier: Modifier) {
    val mainVm: MainViewModel = hiltViewModel()
    val currentTheme by mainVm.currentTheme.collectAsStateWithLifecycle()
    val themeColors = getDataConsColors(currentTheme)
    val displayColor = if (color is Color) color else themeColors.primary
    
    GlassCardSommite(colors = themeColors, modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.Center) {
            Text(text = label.uppercase(), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black, color = themeColors.onSurface.copy(alpha = 0.6f), letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, fontWeight = FontWeight.Black, fontSize = 20.sp, color = displayColor)
        }
    }
}

@Composable
private fun PremiumLockedHistoryCard(colors: DataConsColors) {
    GlassCardSommite(colors = colors) {
        Row(modifier = Modifier.padding(20.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
            Icon(Icons.Rounded.Lock, null, tint = colors.primary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(stringResource(R.string.history_locked_desc), style = MaterialTheme.typography.labelMedium, color = colors.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyAppUsageDetails(
    selectedDateMillis: Long,
    selectedSource: NetworkSource,
    colors: DataConsColors,
    mainVm: MainViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val formatter = remember { DataUsageManager(context) }
    val dailyAppUsage by mainVm.dailyAppUsage.collectAsStateWithLifecycle()

    // Charger les données quand la date ou la source change
    LaunchedEffect(selectedDateMillis, selectedSource) {
        mainVm.loadDailyAppUsage(selectedDateMillis, selectedSource)
    }

    GlassCardSommite(colors = colors) {
        Column(modifier = Modifier.padding(20.dp).fillMaxWidth()) {
            Text(
                stringResource(R.string.daily_app_usage_title, SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(selectedDateMillis))),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                color = colors.primary
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (dailyAppUsage.isEmpty()) {
                Text(
                    stringResource(R.string.no_app_usage_for_day),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurface.copy(alpha = 0.6f)
                )
            } else {
                dailyAppUsage.forEach { appUsage ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = rememberDrawablePainter(appUsage.appIcon),
                            contentDescription = appUsage.appName,
                            modifier = Modifier.size(32.dp).clip(RoundedCornerShape(8.dp))
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            appUsage.appName,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            color = colors.onSurface
                        )
                        Text(
                            formatter.formatData(appUsage.usageBytes),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.primary
                        )
                    }
                }
            }
        }
    }
}
