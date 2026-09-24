package io.arvo.dataconso.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import io.arvo.dataconso.*
import io.arvo.dataconso.R

@Composable
fun QuotasScreen(
    uiState: MainViewModel.UiState,
    colors: DataConsColors,
    viewModel: MainViewModel,
    onToggleVpn: (Boolean, () -> Unit) -> Unit
) {
    var showAdd by remember { mutableStateOf(false) }
    var editingQuota by remember { mutableStateOf<AppQuotaEntity?>(null) }
    val settings by viewModel.settings.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val formatter = remember { DataUsageManager(context) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitleSommite(stringResource(R.string.section_quota_control), Icons.Rounded.SecurityUpdateGood, colors.primary)
            
            GlassCardSommite(colors) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    ControlRowSommite(
                        title = stringResource(R.string.firewall_title),
                        desc = stringResource(R.string.firewall_desc),
                        checked = settings.appFirewallEnabled,
                        icon = Icons.Rounded.Shield,
                        colors = colors
                    ) { onToggleVpn(it) { viewModel.updateAppFirewallStatus(it) } }
                    
                    Row(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        val isActive = settings.appFirewallEnabled
                        val isTunnelActive = uiState.isTunnelActive
                        val statusColor = if (isActive && isTunnelActive) Color(0xFF10B981) else if (isActive) Color(0xFFF59E0B) else Color.Gray
                        Box(modifier = Modifier.size(8.dp).background(statusColor, CircleShape))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isActive && isTunnelActive) stringResource(R.string.firewall_status_active) 
                                   else if (isActive) stringResource(R.string.firewall_status_initializing) 
                                   else stringResource(R.string.firewall_status_idle),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = statusColor,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.appQuotas.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Text(stringResource(R.string.no_quota_active), textAlign = TextAlign.Center, color = colors.onSurface.copy(alpha = 0.5f))
                }
            } else {
                LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    items(uiState.appQuotas, key = { it.packageName }) { quota ->
                        val icon = remember(quota.packageName) {
                            try { context.packageManager.getApplicationIcon(quota.packageName) } catch (_: Exception) { null }
                        }
                        GlassCardSommite(colors) {
                            Row(modifier = Modifier.fillMaxWidth().clickable { editingQuota = quota }.padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (icon != null) {
                                    Image(painter = rememberDrawablePainter(drawable = icon), contentDescription = null, modifier = Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)))
                                    Spacer(modifier = Modifier.width(16.dp))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(quota.appName, fontWeight = FontWeight.Black, fontSize = 18.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                                        
                                        val isBlocked = quota.isBlocked || quota.isManualBlocked
                                        if (isBlocked) {
                                            Surface(
                                                color = Color.Red.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(4.dp),
                                                modifier = Modifier.padding(end = 8.dp)
                                            ) {
                                                Text(
                                                    "STOP", 
                                                    color = Color.Red, 
                                                    style = MaterialTheme.typography.labelSmall, 
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Icon(
                                            imageVector = when(quota.networkType) {
                                                "WIFI" -> Icons.Rounded.Wifi
                                                "MOBILE" -> Icons.Rounded.NetworkCell
                                                else -> Icons.Rounded.AllInclusive
                                            },
                                            contentDescription = null,
                                            tint = colors.primary.copy(alpha = 0.6f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    val progress = if (quota.quotaBytes > 0) (quota.usedBytes.toFloat() / quota.quotaBytes).coerceIn(0f, 1f) else 0f
                                    LinearProgressIndicator(
                                        progress = { progress }, 
                                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).height(8.dp).clip(CircleShape), 
                                        color = if (quota.isBlocked || quota.isManualBlocked || progress >= 1f) Color.Red else colors.primary,
                                        trackColor = colors.onSurface.copy(alpha = 0.1f)
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("${formatter.formatData(quota.usedBytes)} / ${formatter.formatData(quota.quotaBytes)}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
                                        if (quota.isManualBlocked) {
                                            Text(stringResource(R.string.firewall_active), style = MaterialTheme.typography.labelSmall, color = Color.Red, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                IconButton(onClick = { viewModel.toggleManualBlock(quota) }) { 
                                    Icon(
                                        if (quota.isManualBlocked) Icons.Rounded.Block else Icons.Rounded.NetworkCheck, 
                                        null, 
                                        tint = if (quota.isManualBlocked) Color.Red else colors.primary.copy(alpha = 0.6f)
                                    ) 
                                }
                                IconButton(onClick = { viewModel.removeAppQuota(quota) }) { Icon(Icons.Default.Delete, null, tint = colors.onSurface.copy(alpha = 0.3f)) }
                            }
                        }
                    }
                    item { Spacer(modifier = Modifier.height(100.dp)) }
                }
            }
        }
        FloatingActionButton(onClick = { showAdd = true }, modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp), containerColor = colors.primary) { Icon(Icons.Default.Add, null) }
    }
    if (showAdd) AddQuotaDialog(uiState.allInstalledApps, colors, onDismiss = { showAdd = false }) { p, n, l, nt -> viewModel.addAppQuota(p, n, l, nt); showAdd = false }
    if (editingQuota != null) EditQuotaDialog(editingQuota!!, colors, onDismiss = { editingQuota = null }) { b, t -> viewModel.updateAppQuota(editingQuota!!, b, t); editingQuota = null }
}

@Composable
private fun EditQuotaDialog(quota: AppQuotaEntity, colors: DataConsColors, onDismiss: () -> Unit, onConfirm: (Long, String) -> Unit) {
    var unit by remember { mutableStateOf(if (quota.quotaBytes < DataUsageManager.BYTES_PER_GIB) "MiB" else "GiB") }
    var limitInput by remember {
        mutableStateOf(
            if (quota.quotaBytes >= DataUsageManager.BYTES_PER_GIB) {
                (quota.quotaBytes / DataUsageManager.BYTES_PER_GIB.toDouble()).toString()
            } else {
                (quota.quotaBytes / DataUsageManager.BYTES_PER_MIB.toDouble()).toString()
            }
        )
    }
    
    var type by remember { mutableStateOf(quota.networkType) }
    Dialog(onDismissRequest = onDismiss) {
        GlassCardSommite(colors = colors, modifier = Modifier.fillMaxWidth().wrapContentHeight()) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.Edit, null, tint = colors.primary, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(stringResource(R.string.edit_limit), fontWeight = FontWeight.Black, fontSize = 20.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = limitInput, 
                        onValueChange = { limitInput = it }, 
                        label = { Text("Valeur") }, 
                        modifier = Modifier.weight(1f), 
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colors.primary, cursorColor = colors.primary)
                    )
                    Row(modifier = Modifier.clip(CircleShape).background(colors.primary.copy(alpha = 0.1f)).padding(4.dp)) {
                        listOf("MiB", "GiB").forEach { u ->
                            Box(modifier = Modifier.clip(CircleShape).background(if (unit == u) colors.primary else Color.Transparent).clickable { unit = u }.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                Text(u, color = if (unit == u) Color.White else colors.onSurface.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(stringResource(R.string.network_type_label), style = MaterialTheme.typography.labelMedium)
                Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("MOBILE", "WIFI", "BOTH").forEach { t ->
                        FilterChip(
                            selected = type == t,
                            onClick = { type = t },
                            label = { Text(if (t == "BOTH") stringResource(R.string.network_both) else if (t == "WIFI") "WIFI" else "MOBILE") },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = colors.primary, selectedLabelColor = Color.White)
                        )
                    }
                }
                Button(
                    onClick = { 
                        val factor = if (unit == "GiB") DataUsageManager.BYTES_PER_GIB else DataUsageManager.BYTES_PER_MIB
                        onConfirm(((limitInput.toDoubleOrNull() ?: 1.0) * factor).toLong(), type) 
                    }, 
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                ) { Text(stringResource(R.string.update_button)) }
            }
        }
    }
}

@Composable
private fun AddQuotaDialog(apps: List<AppUsageInfo>, colors: DataConsColors, onDismiss: () -> Unit, onConfirm: (String, String, Long, String) -> Unit) {
    var search by remember { mutableStateOf("") }
    var selectedApp by remember { mutableStateOf<AppUsageInfo?>(null) }
    var unit by remember { mutableStateOf("GiB") }
    var limitInput by remember { mutableStateOf("1.0") }
    var type by remember { mutableStateOf("BOTH") }
    
    val filtered = apps.filter { it.appName.contains(search, ignoreCase = true) }

    Dialog(onDismissRequest = onDismiss) {
        GlassCardSommite(colors = colors, modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f)) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (selectedApp == null) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Search, null, tint = colors.primary, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(stringResource(R.string.search_app), fontWeight = FontWeight.Black, fontSize = 20.sp)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    OutlinedTextField(
                        value = search, 
                        onValueChange = { search = it }, 
                        label = { Text(stringResource(R.string.search_app)) }, 
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colors.primary, cursorColor = colors.primary)
                    )
                    LazyColumn(modifier = Modifier.weight(1f).padding(top = 16.dp)) {
                        items(filtered) { app ->
                            Row(modifier = Modifier.fillMaxWidth().clickable { selectedApp = app }.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(app.appName, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Icon(Icons.Rounded.ChevronRight, null, tint = colors.primary.copy(alpha = 0.5f))
                            }
                        }
                    }
                } else {
                    Text(stringResource(R.string.activate_limit), fontWeight = FontWeight.Black, fontSize = 20.sp)
                    Text(selectedApp!!.appName, style = MaterialTheme.typography.bodyLarge, color = colors.primary)
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = limitInput, 
                            onValueChange = { limitInput = it }, 
                            label = { Text("Valeur") }, 
                            modifier = Modifier.weight(1f), 
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = colors.primary, cursorColor = colors.primary)
                        )
                        Row(modifier = Modifier.clip(CircleShape).background(colors.primary.copy(alpha = 0.1f)).padding(4.dp)) {
                            listOf("MiB", "GiB").forEach { u ->
                                Box(modifier = Modifier.clip(CircleShape).background(if (unit == u) colors.primary else Color.Transparent).clickable { unit = u }.padding(horizontal = 12.dp, vertical = 6.dp)) {
                                    Text(u, color = if (unit == u) Color.White else colors.onSurface.copy(alpha = 0.6f), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(stringResource(R.string.network_type_label), style = MaterialTheme.typography.labelMedium)
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("MOBILE", "WIFI", "BOTH").forEach { t ->
                            FilterChip(
                                selected = type == t,
                                onClick = { type = t },
                                label = { Text(if (t == "BOTH") stringResource(R.string.network_both) else if (t == "WIFI") "WIFI" else "MOBILE") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = colors.primary, selectedLabelColor = Color.White)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedButton(onClick = { selectedApp = null }, modifier = Modifier.weight(1f).height(56.dp)) { Text(stringResource(android.R.string.cancel)) }
                        Button(
                            onClick = { 
                                val factor = if (unit == "GiB") DataUsageManager.BYTES_PER_GIB else DataUsageManager.BYTES_PER_MIB
                                onConfirm(selectedApp!!.packageName, selectedApp!!.appName, ((limitInput.toDoubleOrNull() ?: 1.0) * factor).toLong(), type) 
                            }, 
                            modifier = Modifier.weight(1f).height(56.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.primary)
                        ) { Text(stringResource(R.string.update_button)) }
                    }
                }
            }
        }
    }
}
