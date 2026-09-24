package io.arvo.dataconso.ui.dashboard

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.arvo.dataconso.*
import io.arvo.dataconso.R
import io.arvo.dataconso.ui.*

@Composable
fun DashboardScreen(
    colors: DataConsColors
) {
    val context = LocalContext.current
    val formatter = remember { DataUsageManager(context) }
    val mainVm: MainViewModel = hiltViewModel()
    val settings by mainVm.settings.collectAsStateWithLifecycle()
    
    // Sommité : Observation directe pour garantir la réactivité instantanée
    val currentSource by mainVm.currentSource.collectAsStateWithLifecycle()
    val currentGranularity by mainVm.currentGranularity.collectAsStateWithLifecycle()
    
    val fastState by mainVm.realtimeState.collectAsStateWithLifecycle()
    val slowState by mainVm.analysisState.collectAsStateWithLifecycle()
    val uiState by mainVm.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            SourceSelector(currentSource, { mainVm.setSource(it) }, colors, Modifier.weight(1f))
            
            if (settings.totalMoneySaved > 0) {
                Spacer(modifier = Modifier.width(12.dp))
                Surface(
                    color = Color(0xFF10B981).copy(alpha = 0.1f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFF10B981).copy(alpha = 0.3f)),
                    onClick = { mainVm.shareSavings() }
                ) {
                    Row(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.Savings, null, tint = Color(0xFF10B981), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.money_saved_badge, settings.totalMoneySaved),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF10B981)
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        GranularitySelector(currentGranularity, { mainVm.setGranularity(it) }, colors)
        
        Spacer(modifier = Modifier.height(32.dp))

        // JAUGE RAPIDE (Real-time)
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                LuminousGaugeSommite(
                    used = fastState.displayUsedGb,
                    quota = fastState.displayQuotaGb,
                    primaryColor = colors.primary,
                    isLowPerf = settings.lowPerformanceMode,
                    onSurfaceColor = colors.onSurface,
                    outlineColor = colors.outline
                )
                
                if (fastState.surplusGb > 0) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Surface(
                        color = Color(0xFF6366F1).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.3f))
                    ) {
                        Text(
                            text = "RÉSERVE ACCUMULÉE : +${String.format(java.util.Locale.US, "%.2f", fastState.surplusGb)} Go",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF6366F1)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.history_title, stringResource(currentGranularity.labelRes).uppercase()),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = colors.primary,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        // GRAPHIQUE LENT
        GlassCardSommite(
            colors = colors, 
            modifier = Modifier.fillMaxWidth().height(220.dp)
        ) {
            io.arvo.dataconso.ui.components.ArvoGlassChart(
                history = slowState.history,
                source = currentSource,
                granularity = currentGranularity,
                colors = colors,
                modifier = Modifier.fillMaxSize().padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // VITESSE RAPIDE
        SpeedMonitorCard(
            fastState = fastState,
            tColors = colors,
            formatter = formatter
        )

        Spacer(modifier = Modifier.height(24.dp))

        // DEBUG INFO (Temporaire pour identifier le vide)
        if (fastState.todayUsedGb == 0.0) {
            Text("Debug: Today Used is 0. Source: $currentSource", color = Color.Red, fontSize = 10.sp)
        }

        // APPS LENTES (Maintenant fusionnées avec le live via uiState)
        TopAppsSection(uiState.topApps, colors, formatter)

        // IA LENTE
        if (slowState.insights.isNotEmpty()) {
            Spacer(modifier = Modifier.height(32.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.AutoAwesome, null, tint = Color(0xFFF59E0B), modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.section_smart_assist),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    color = colors.onSurface.copy(alpha = 0.6f),
                    letterSpacing = 1.sp
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            slowState.insights.forEach { insight ->
                DashboardInsightCard(insight, colors) {
                    insight.actionId?.let { mainVm.performInsightAction(it) }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(100.dp))
    }
}

@Composable
private fun DashboardInsightCard(insight: ArvoInsight, colors: DataConsColors, onClick: () -> Unit) {
    val tint = when(insight.type) {
        InsightType.WARNING -> Color(0xFFFF5252)
        InsightType.SUGGESTION -> Color(0xFFFFD740)
        InsightType.ECONOMY -> Color(0xFF69F0AE)
        InsightType.SECURITY -> Color(0xFF40C4FF)
    }
    GlassCardSommite(colors, onClick = if (insight.actionId != null) onClick else null) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(modifier = Modifier.size(36.dp), shape = CircleShape, color = tint.copy(alpha = 0.15f)) {
                Icon(
                    imageVector = when(insight.type) {
                        InsightType.WARNING -> Icons.Rounded.Warning
                        InsightType.SUGGESTION -> Icons.Rounded.Lightbulb
                        InsightType.ECONOMY -> Icons.Rounded.Savings
                        InsightType.SECURITY -> Icons.Rounded.Security
                    },
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.padding(8.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(insight.title, fontWeight = FontWeight.Black, fontSize = 15.sp, color = colors.onSurface)
                Text(insight.description, style = MaterialTheme.typography.bodySmall, color = colors.onSurface.copy(alpha = 0.7f))
                if (insight.actionId != null) {
                    Text("AGIR MAINTENANT", style = MaterialTheme.typography.labelSmall, color = tint, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                }
            }
        }
    }
}

@Composable
fun SourceSelector(selected: NetworkSource, onSelect: (NetworkSource) -> Unit, colors: DataConsColors, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(colors.surface.copy(alpha = 0.4f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        NetworkSource.entries.forEach { source ->
            val isSelected = selected == source
            val icon = when(source) {
                NetworkSource.MOBILE -> Icons.Rounded.SignalCellularAlt
                NetworkSource.WIFI -> Icons.Rounded.Wifi
                NetworkSource.TOTAL -> Icons.Rounded.AllInclusive
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) colors.primary else Color.Transparent)
                    .clickable { onSelect(source) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(icon, null, modifier = Modifier.size(16.dp), tint = if (isSelected) Color.White else colors.onSurface.copy(alpha = 0.6f))
                    Text(
                        text = stringResource(source.labelRes),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                        color = if (isSelected) Color.White else colors.onSurface.copy(alpha = 0.7f),
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
fun GranularitySelector(selected: Granularity, onSelect: (Granularity) -> Unit, colors: DataConsColors) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(colors.surface.copy(alpha = 0.4f)).padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Granularity.entries.forEach { gran ->
            val isSelected = selected == gran
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) colors.primary else Color.Transparent)
                    .clickable { onSelect(gran) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(gran.labelRes).uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = if (isSelected) FontWeight.Black else FontWeight.Bold,
                    color = if (isSelected) Color.White else colors.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun SpeedMonitorCard(fastState: MainViewModel.RealtimeState, tColors: DataConsColors, formatter: DataUsageManager) {
    GlassCardSommite(colors = tColors) {
        Row(
            modifier = Modifier.padding(20.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SpeedItemPremium(stringResource(R.string.speed_down), formatter.formatSpeed(fastState.dlSpeed), Color(0xFF4ADE80))
            Box(modifier = Modifier.width(1.dp).height(30.dp).background(tColors.outline.copy(alpha = 0.2f)))
            SpeedItemPremium(stringResource(R.string.speed_up), formatter.formatSpeed(fastState.ulSpeed), Color(0xFFF87171))
        }
    }
}

@Composable
fun SpeedItemPremium(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = color.copy(alpha = 0.7f))
        Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
    }
}

@Composable
fun TopAppsSection(apps: List<AppUsageInfo>, colors: DataConsColors, formatter: DataUsageManager) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Rounded.Whatshot, null, tint = Color(0xFFFF5722), modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.section_top_apps).uppercase(),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = colors.onSurface.copy(alpha = 0.6f),
                letterSpacing = 1.sp
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        if (apps.isEmpty()) {
            GlassCardSommite(colors = colors) {
                Text(stringResource(R.string.history_empty), modifier = Modifier.padding(24.dp).fillMaxWidth(), textAlign = TextAlign.Center)
            }
        } else {
            apps.take(5).forEach { app ->
                AppUsageItem(app, colors, formatter)
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}

@Composable
fun AppUsageItem(app: AppUsageInfo, colors: DataConsColors, formatter: DataUsageManager) {
    val context = LocalContext.current
    val icon = remember(app.packageName) { try { context.packageManager.getApplicationIcon(app.packageName) } catch (_: Exception) { null } }

    GlassCardSommite(colors = colors) {
        Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Image(painter = rememberDrawablePainter(drawable = icon), contentDescription = null, modifier = Modifier.size(36.dp).clip(RoundedCornerShape(6.dp)), contentScale = ContentScale.Fit)
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(app.appName, fontWeight = FontWeight.Black, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(app.packageName, style = MaterialTheme.typography.labelSmall, color = colors.onSurface.copy(alpha = 0.5f), maxLines = 1)
            }
            Text(formatter.formatData(app.bytes), fontWeight = FontWeight.Black, color = colors.primary, fontSize = 18.sp)
        }
    }
}
