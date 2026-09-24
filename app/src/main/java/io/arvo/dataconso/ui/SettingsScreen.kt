package io.arvo.dataconso.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VpnLock
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import io.arvo.dataconso.*
import io.arvo.dataconso.R
import io.arvo.dataconso.ui.onboarding.PermissionViewModel

private fun android.content.Context.openSettings(intent: Intent) {
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    } else {
        startActivity(Intent(android.provider.Settings.ACTION_SETTINGS))
    }
}

@Composable
fun SettingsScreen(
    settings: AppSettings,
    viewModel: MainViewModel,
    colors: DataConsColors,
    uiState: MainViewModel.UiState,
    permState: PermissionViewModel.PermissionState,
    onNavigateToGhostMode: () -> Unit,
    onToggleVpn: (Boolean, () -> Unit) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var vpnPermissionMissing by remember {
        mutableStateOf(android.net.VpnService.prepare(context) != null)
    }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                vpnPermissionMissing = android.net.VpnService.prepare(context) != null
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp).verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(24.dp))
        
        if (uiState.hasVpnConflict) {
            VpnConflictWarning(colors)
            Spacer(modifier = Modifier.height(16.dp))
        }

        SectionTitleSommite("COMPTE CLOUD", Icons.Rounded.CloudSync, colors.primary)
        GlassCardSommite(colors) {
            Row(modifier = Modifier.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = colors.primary.copy(alpha = 0.1f)) {
                    Icon(Icons.Rounded.CloudDone, null, tint = colors.primary, modifier = Modifier.padding(10.dp))
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(settings.userEmail ?: "Sauvegarder mes économies", fontWeight = FontWeight.Bold)
                    Text(if (settings.userEmail != null) "Synchronisation active" else "Lie tes données au Cloud", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                }
                if (settings.userEmail == null) {
                    Button(onClick = { viewModel.signInAnonymously() }, shape = RoundedCornerShape(12.dp)) {
                        Text("LIER")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitleSommite(stringResource(R.string.settings_title), Icons.Rounded.Settings, colors.primary)
        SettingsCardSommite(settings, viewModel, colors)
        
        Spacer(modifier = Modifier.height(32.dp))
        SectionTitleSommite(stringResource(R.string.language_title), Icons.Rounded.Language, colors.primary)
        GlassCardSommite(colors) {
            LazyRow(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(MainActivity.APP_LANGUAGES) { (code, flag, resId) ->
                    FilterChip(
                        selected = settings.selectedLanguage == code, 
                        onClick = { viewModel.setLanguage(code) }, 
                        label = { Text("$flag ${stringResource(resId)}") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        SectionTitleSommite("MODE PREMIUM", Icons.Rounded.Star, colors.primary)
        
        Button(
            onClick = { viewModel.togglePremiumStatus() },
            modifier = Modifier.fillMaxWidth().height(40.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colors.primary.copy(alpha = 0.1f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("TEST: SWITCH PREMIUM/FREE", color = colors.primary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))

        PremiumUpgradeCard(settings.isEliteActive, colors) {
            viewModel.togglePremiumStatus()
        }

        Spacer(modifier = Modifier.height(32.dp))
        SectionTitleSommite(stringResource(R.string.security_title), Icons.Rounded.Security, colors.primary)
        GlassCardSommite(colors) { 
            Column { 
                if (vpnPermissionMissing) {
                    Text(
                        "Autorisation VPN requise pour activer les blocages",
                        color = Color(0xFFF59E0B),
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                    )
                    Button(
                        onClick = {
                            android.net.VpnService.prepare(context)?.let {
                                context.startActivity(it)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)
                    ) {
                        Text("AUTORISER LA PROTECTION")
                    }
                }
                if (uiState.hasVpnConflict) {
                    OutlinedButton(
                        onClick = {
                            context.openSettings(Intent(android.provider.Settings.ACTION_VPN_SETTINGS))
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 4.dp)
                    ) {
                        Text("GÉRER LES VPN ACTIFS")
                    }
                }
                ControlRowSommite(stringResource(R.string.vpn_protection_label), stringResource(R.string.vpn_protection_desc), settings.vpnEnabled, Icons.Default.VpnLock, colors) { active -> 
                    onToggleVpn(active) { viewModel.updateVpnStatus(active) }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = colors.outline)
                ControlRowSommite(stringResource(R.string.speed_monitor_label), stringResource(R.string.speed_monitor_desc), settings.speedEnabled, Icons.Default.Speed, colors) { active -> 
                    viewModel.updateSpeedStatus(active)
                } 
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = colors.outline)
                ControlRowSommite(stringResource(R.string.ghost_mode_label), stringResource(R.string.ghost_mode_desc), settings.ghostModeEnabled, Icons.Rounded.Security, colors, onClick = onNavigateToGhostMode) { active -> 
                    onToggleVpn(active) { viewModel.updateGhostModeStatus(active) }
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp), color = colors.outline)
                ControlRowSommite(stringResource(R.string.performance_mode_label), stringResource(R.string.performance_mode_desc), settings.lowPerformanceMode, Icons.Rounded.Speed, colors) { active ->
                    viewModel.updatePerformanceMode(active)
                }
            } 
        }
        
        if (!permState.hasUsageStats || !permState.isIgnoringBattery) { 
            Spacer(modifier = Modifier.height(32.dp))
            SectionTitleSommite(stringResource(R.string.system_optimization), Icons.Rounded.Warning, Color.Red)
            if (!permState.hasUsageStats) {
                Button(onClick = { context.openSettings(Intent(android.provider.Settings.ACTION_USAGE_ACCESS_SETTINGS)) }, modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 4.dp), colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                    Text(stringResource(R.string.authorize_usage_stats).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
            if (!permState.isIgnoringBattery) {
                Button(
                    onClick = { 
                        val i = Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, Uri.parse("package:${context.packageName}"))
                        context.openSettings(i)
                    }, 
                    modifier = Modifier.fillMaxWidth().height(56.dp).padding(vertical = 4.dp), 
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF59E0B))
                ) {
                    Text(stringResource(R.string.ignore_battery).uppercase(), fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
fun SettingsCardSommite(settings: AppSettings, viewModel: MainViewModel, colors: DataConsColors) { 
    val ctx = LocalContext.current
    var w by remember(settings.monthlyWifiGb) { mutableStateOf(settings.monthlyWifiGb.toString()) }
    var m by remember(settings.monthlyMobileGb) { mutableStateOf(settings.monthlyMobileGb.toString()) }
    var d by remember(settings.billingCycleDay) { mutableStateOf(settings.billingCycleDay.toString()) }
    
    val activeSims by viewModel.simSubscriptions.collectAsStateWithLifecycle()

    GlassCardSommite(colors) { 
        Column(modifier = Modifier.padding(24.dp)) { 
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) { 
                OutlinedTextField(value = m, onValueChange = { m = it }, label = { Text(stringResource(R.string.source_mobile)) }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = d, onValueChange = { d = it }, label = { Text(stringResource(R.string.reset_day_label)) }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(value = w, onValueChange = { w = it }, label = { Text(stringResource(R.string.wifi_label)) }, modifier = Modifier.fillMaxWidth())
            
            if (activeSims.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                activeSims.forEach { sim ->
                    if (sim.isDefaultData) {
                        Text(
                            text = stringResource(R.string.active_sim_label, sim.carrierName),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Button(
                onClick = { viewModel.updateConfig(w.toDoubleOrNull() ?: 50.0, m.toDoubleOrNull() ?: 5.0, d.toIntOrNull() ?: 1) }, 
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp).height(56.dp)
            ) { Text(stringResource(R.string.apply_changes)) }
        } 
    } 
}

@Composable
fun PremiumUpgradeCard(isPremium: Boolean, colors: DataConsColors, onUpgrade: () -> Unit) {
    GlassCardSommite(colors) {
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Rounded.Star, null, tint = if (isPremium) Color(0xFFFFD700) else colors.primary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(12.dp))
            Text(if (isPremium) stringResource(R.string.premium_member) else "ARVO ELITE", fontWeight = FontWeight.Black, fontSize = 20.sp)
            Text(
                if (isPremium) stringResource(R.string.premium_desc_unlocked) 
                else stringResource(R.string.premium_desc_locked),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            if (!isPremium) {
                Button(onClick = onUpgrade, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp)) {
                    Text(stringResource(R.string.premium_upgrade))
                }
            }
        }
    }
}

@Composable
fun VpnConflictWarning(colors: DataConsColors) {
    GlassCardSommite(colors) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Warning, null, tint = Color.Red, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(stringResource(R.string.vpn_conflict_title), fontWeight = FontWeight.Black, color = Color.Red, fontSize = 14.sp)
                Text(stringResource(R.string.vpn_conflict_desc), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun ThemeSelector(onThemeChange: (AppTheme) -> Unit) {
    var exp by remember { mutableStateOf(false) }
    Box { 
        IconButton(onClick = { exp = true }) { Icon(Icons.Default.Palette, null, tint = MaterialTheme.colorScheme.primary) }
        DropdownMenu(expanded = exp, onDismissRequest = { exp = false }) { 
            AppTheme.entries.forEach { t -> DropdownMenuItem(text = { Text(t.label) }, onClick = { onThemeChange(t); exp = false }) } 
        } 
    } 
}
