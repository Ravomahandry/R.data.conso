package com.datamgmt.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.provider.Settings
import android.telephony.SubscriptionInfo
import android.telephony.TelephonyManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    private val vpnLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            startService(Intent(this, VpnBlockService::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        scheduleUsageWorker()

        setContent {
            val usageManager = DataUsageManager(this)
            var monthlyQuota by remember { mutableStateOf("30") }
            var settingsLoaded by remember { mutableStateOf(false) }
            var hasUsageStatsPermission by remember { mutableStateOf(checkUsageStatsPermission()) }
            var hasReadPhoneState by remember { mutableStateOf(false) }

            val subscriptions = remember { mutableStateListOf<com.datamgmt.myapplication.SubscriptionInfoWrapper>() }
            var selectedSimIndex by remember { mutableIntStateOf(0) }
            var selectedPeriod by remember { mutableStateOf(DataUsageManager.PeriodType.DAILY) }
            var selectedDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
            var showDatePicker by remember { mutableStateOf(false) }
            var uploadBytes by remember { mutableLongStateOf(0L) }
            var downloadBytes by remember { mutableLongStateOf(0L) }
            var sim1Usage by remember { mutableStateOf(DataUsageManager.UsageBreakdown()) }
            var sim2Usage by remember { mutableStateOf(DataUsageManager.UsageBreakdown()) }
            var combinedUsage by remember { mutableStateOf(DataUsageManager.UsageBreakdown()) }
            val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }

            val database = AppDatabase.getDatabase(this)

            LaunchedEffect(Unit) {
                val s = database.settingsDao().getSettings()
                s?.let {
                    monthlyQuota = it.monthlyQuotaGb.toString()
                }

                // request read phone state if not granted
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.READ_PHONE_STATE) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.READ_PHONE_STATE), 101)
                } else {
                    hasReadPhoneState = true
                }

                // Request notification permission on Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.POST_NOTIFICATIONS) != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 102)
                    }
                }

                // load subscriptions
                val subs = usageManager.getActiveSubscriptions()
                subscriptions.clear()
                if (subs.isEmpty()) {
                    subscriptions.add(SubscriptionInfoWrapper("TOTAL", "Total", null))
                } else {
                    for ((index, sub) in subs.take(2).withIndex()) {
                        val id = resolveSubscriberId(sub)
                        val carrier = sub.carrierName?.toString()?.takeIf { it.isNotBlank() } ?: "SIM"
                        subscriptions.add(SubscriptionInfoWrapper(id, "SIM${index + 1} - $carrier", sub))
                    }
                    subscriptions.add(SubscriptionInfoWrapper("TOTAL", "Combine", null))
                }

                settingsLoaded = true
            }

            // auto-refresh
            LaunchedEffect(selectedPeriod, selectedSimIndex, selectedDateMillis, settingsLoaded, subscriptions.size) {
                while (true) {
                    fun loadUsage(subscriberId: String?): DataUsageManager.UsageBreakdown {
                        return if (selectedPeriod == DataUsageManager.PeriodType.CUSTOM_DATE) {
                            usageManager.getUsageBreakdownForDay(selectedDateMillis, subscriberId)
                        } else {
                            usageManager.getUsageBreakdownForPeriod(selectedPeriod, subscriberId)
                        }
                    }

                    val simSubscriptions = subscriptions.filter { it.id != "TOTAL" }.take(2)
                    val sim1 = simSubscriptions.getOrNull(0)?.let { loadUsage(it.id) } ?: DataUsageManager.UsageBreakdown()
                    val sim2 = simSubscriptions.getOrNull(1)?.let { loadUsage(it.id) } ?: DataUsageManager.UsageBreakdown()
                    val combined = if (simSubscriptions.isNotEmpty()) {
                        DataUsageManager.UsageBreakdown(
                            downloadBytes = sim1.downloadBytes + sim2.downloadBytes,
                            uploadBytes = sim1.uploadBytes + sim2.uploadBytes
                        )
                    } else {
                        loadUsage(null)
                    }

                    val sub = subscriptions.getOrNull(selectedSimIndex)
                    val selectedUsage = if (sub?.id == "TOTAL") {
                        combined
                    } else {
                        loadUsage(sub?.id)
                    }

                    sim1Usage = sim1
                    sim2Usage = sim2
                    combinedUsage = combined
                    downloadBytes = selectedUsage.downloadBytes
                    uploadBytes = selectedUsage.uploadBytes
                    delay(15_000)
                }
            }

            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = getString(R.string.app_name),
                            modifier = Modifier.size(52.dp)
                        )
                        Text(text = getString(R.string.app_title), style = MaterialTheme.typography.headlineMedium)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Period selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())
                    ) {
                        listOf(
                            DataUsageManager.PeriodType.DAILY,
                            DataUsageManager.PeriodType.WEEKLY,
                            DataUsageManager.PeriodType.MONTHLY,
                            DataUsageManager.PeriodType.CUMULATIVE,
                            DataUsageManager.PeriodType.CUSTOM_DATE
                        ).forEach { p ->
                            val selected = p == selectedPeriod
                            val label = when (p) {
                                DataUsageManager.PeriodType.DAILY -> "Jour"
                                DataUsageManager.PeriodType.WEEKLY -> "Semaine"
                                DataUsageManager.PeriodType.MONTHLY -> "Mois"
                                DataUsageManager.PeriodType.CUMULATIVE -> "Cumul"
                                DataUsageManager.PeriodType.CUSTOM_DATE -> "Date"
                            }
                            Chip(selected = selected, onClick = {
                                selectedPeriod = p
                                if (p == DataUsageManager.PeriodType.CUSTOM_DATE) {
                                    showDatePicker = true
                                }
                            }, label = label)
                        }
                    }

                    if (selectedPeriod == DataUsageManager.PeriodType.CUSTOM_DATE) {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Date selectionnee : ${dateFormatter.format(Date(selectedDateMillis))}")
                        }
                    }

                    if (showDatePicker) {
                        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = selectedDateMillis)
                        DatePickerDialog(
                            onDismissRequest = { showDatePicker = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    selectedDateMillis = datePickerState.selectedDateMillis ?: selectedDateMillis
                                    selectedPeriod = DataUsageManager.PeriodType.CUSTOM_DATE
                                    showDatePicker = false
                                }) {
                                    Text("OK")
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDatePicker = false }) {
                                    Text("Annuler")
                                }
                            }
                        ) {
                            DatePicker(state = datePickerState)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // SIM selector
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        subscriptions.forEachIndexed { idx, s ->
                            val sel = idx == selectedSimIndex
                            Box(modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (sel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                .clickable { selectedSimIndex = idx }
                                .padding(12.dp)) {
                                Text(text = s.displayName, color = if (sel) Color.White else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Consommation par SIM", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatsCard(title = "SIM1", value = DataUsageManager.humanReadable(sim1Usage.totalBytes), modifier = Modifier.weight(1f))
                        StatsCard(title = "SIM2", value = DataUsageManager.humanReadable(sim2Usage.totalBytes), modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    StatsCard(title = "SIM1 + SIM2", value = DataUsageManager.humanReadable(combinedUsage.totalBytes), modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats cards
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        StatsCard(title = "Download", value = DataUsageManager.humanReadable(downloadBytes), modifier = Modifier.weight(1f))
                        StatsCard(title = "Upload", value = DataUsageManager.humanReadable(uploadBytes), modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    StatsCard(title = "Total", value = DataUsageManager.humanReadable(downloadBytes + uploadBytes), modifier = Modifier.fillMaxWidth())

                    Spacer(modifier = Modifier.height(12.dp))

                    // Graphique consommation
                    Box(modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()) {
                        UsageLineChart()
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = monthlyQuota,
                        onValueChange = { monthlyQuota = it },
                        label = { Text(getString(R.string.quota_label)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val quota = monthlyQuota.toDoubleOrNull() ?: 30.0
                            lifecycleScope.launch {
                                val settings = database.settingsDao().getSettings() ?: AppSettings()
                                database.settingsDao().saveSettings(settings.copy(monthlyQuotaGb = quota))
                            }
                        }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text("Enregistrer le quota")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!hasUsageStatsPermission) {
                        Button(onClick = { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }, modifier = Modifier.fillMaxWidth()) {
                            Text(getString(R.string.authorize_usage_stats))
                        }
                    }

                    Button(onClick = {
                        val vpnIntent = VpnService.prepare(this@MainActivity)
                        if (vpnIntent != null) {
                            vpnLauncher.launch(vpnIntent)
                        } else {
                            startService(Intent(this@MainActivity, VpnBlockService::class.java))
                        }
                    }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                        Text(getString(R.string.enable_vpn_protection))
                    }
                }
            }
        }
    }

    private fun checkUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, Process.myUid(), packageName)
        return mode == AppOpsManager.MODE_ALLOWED
    }

    private fun scheduleUsageWorker() {
        val workRequest = PeriodicWorkRequestBuilder<UsageWorker2>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "UsageTracking",
            ExistingPeriodicWorkPolicy.KEEP,
            workRequest
        )
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    private fun resolveSubscriberId(subscriptionInfo: SubscriptionInfo): String {
        return try {
            val telephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            telephonyManager
                .createForSubscriptionId(subscriptionInfo.subscriptionId)
                .subscriberId
                ?.takeIf { it.isNotBlank() }
                ?: subscriptionInfo.iccId
                ?: subscriptionInfo.subscriptionId.toString()
        } catch (e: Exception) {
            subscriptionInfo.iccId ?: subscriptionInfo.subscriptionId.toString()
        }
    }

}

// Small UI helpers
@Composable
fun Chip(selected: Boolean, onClick: () -> Unit, label: String) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
        .clickable { onClick() }
        .padding(horizontal = 12.dp, vertical = 8.dp)) {
        Text(text = label, color = if (selected) Color.White else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
fun StatsCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = title, style = MaterialTheme.typography.labelLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, style = MaterialTheme.typography.headlineSmall)
        }
    }
}

data class SubscriptionInfoWrapper(val id: String, val displayName: String, val raw: android.telephony.SubscriptionInfo?)
