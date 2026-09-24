package io.arvo.dataconso

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.Balance
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.WarningAmber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.arvo.dataconso.R
import io.arvo.dataconso.ui.DataConsColors
import io.arvo.dataconso.ui.GlassCardSommite
import io.arvo.dataconso.ui.getDataConsColors
import java.util.Locale

@Composable
fun SimulationScreen(viewModel: MainViewModel? = null) {
    val mainVm: MainViewModel = hiltViewModel()
    val uiState by mainVm.uiState.collectAsStateWithLifecycle()
    val settings by mainVm.settings.collectAsStateWithLifecycle()
    val currentTheme by mainVm.currentTheme.collectAsStateWithLifecycle()
    
    val colors = getDataConsColors(currentTheme)
    
    var quota by remember(settings.monthlyMobileGb) { mutableDoubleStateOf(settings.monthlyMobileGb) }
    // Rigueur CEO : Initialisation sur la consommation du MOIS (Cycle) et non de la journée
    var currentUsage by remember(uiState.monthUsedGb) { mutableDoubleStateOf(uiState.monthUsedGb) }
    var daysLeft by remember { mutableIntStateOf(QuotaCalculator.calculateDaysRemaining(settings.billingCycleDay)) }

    val dataRemaining = (quota - currentUsage).coerceAtLeast(0.0)
    val suggestedDaily = if (daysLeft > 0) dataRemaining / daysLeft else 0.0
    val estimatedEnd = currentUsage + (suggestedDaily * daysLeft)
    val overBudget = estimatedEnd > quota
    
    val progress = if (quota > 0) (currentUsage / quota).toFloat() else 0f
    val animatedProgress by animateFloatAsState(targetValue = progress.coerceIn(0f, 1f), animationSpec = tween(1000), label = "")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp)) // Réduction de l'espace
        
        // Titre Premium
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Surface(modifier = Modifier.size(44.dp), shape = CircleShape, color = colors.primary.copy(alpha = 0.1f)) {
                Icon(Icons.Rounded.Analytics, null, tint = colors.primary, modifier = Modifier.padding(10.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(stringResource(R.string.simulation_title), fontWeight = FontWeight.Black, fontSize = 22.sp)
                Text(stringResource(R.string.simulation_desc), style = MaterialTheme.typography.labelSmall, color = colors.onSurface.copy(alpha = 0.5f))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Card des Sliders / Inputs
        GlassCardSommite(colors = colors) {
            Column(modifier = Modifier.padding(16.dp)) {
                SimulationInputRow(stringResource(R.string.sim_quota_label), quota, Icons.Rounded.Balance, colors, false) { quota = it }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.outline)
                SimulationInputRow(stringResource(R.string.sim_usage_label), currentUsage, Icons.Rounded.Analytics, colors, false) { currentUsage = it }
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = colors.outline)
                SimulationInputRow(stringResource(R.string.sim_days_label), daysLeft.toDouble(), Icons.Rounded.CalendarMonth, colors, true) { daysLeft = it.toInt() }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Sommité : Nouvelles cartes pour remplir l'espace (Ajustement design)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            GlassCardSommite(colors = colors, modifier = Modifier.weight(1f).height(100.dp)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
                    Text(
                        text = stringResource(R.string.sim_data_rem), 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = stringResource(R.string.unit_gb, dataRemaining), 
                        fontWeight = FontWeight.Black, 
                        fontSize = 18.sp, 
                        color = if (dataRemaining < 1.0) Color.Red else Color(0xFF10B981)
                    )
                }
            }
            GlassCardSommite(colors = colors, modifier = Modifier.weight(1f).height(100.dp)) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.Center) {
                    Text(
                        text = stringResource(R.string.sim_run_rate), 
                        style = MaterialTheme.typography.labelSmall, 
                        fontWeight = FontWeight.Bold,
                        color = colors.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val runRate = if (quota > 0) (currentUsage / quota * 100).toInt() else 0
                    Text(
                        text = "$runRate%", 
                        fontWeight = FontWeight.Black, 
                        fontSize = 20.sp, 
                        color = colors.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Résultats Visuels
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
            Text(stringResource(R.string.sim_pred_results), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = colors.primary)
            Surface(shape = CircleShape, color = (if (overBudget) Color.Red else Color(0xFF10B981)).copy(alpha = 0.1f)) {
                Text(
                    if (overBudget) stringResource(R.string.sim_status_risk_label) else stringResource(R.string.sim_status_optimal_label),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (overBudget) Color.Red else Color(0xFF10B981)
                )
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        GlassCardSommite(colors = colors) {
            Column(modifier = Modifier.padding(16.dp)) { // Réduction padding
                // Barre de progression prédictive
                Box(modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape).background(colors.onSurface.copy(alpha = 0.05f))) {
                    Box(modifier = Modifier.fillMaxWidth(animatedProgress).fillMaxHeight().clip(CircleShape).background(
                        Brush.horizontalGradient(listOf(colors.primary, if (overBudget) Color.Red else colors.primary.copy(alpha = 0.6f)))
                    ))
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(stringResource(R.string.sim_daily_suggested).uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(stringResource(R.string.unit_gb, suggestedDaily), fontWeight = FontWeight.Black, fontSize = 20.sp, color = colors.primary)
                    }
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text(stringResource(R.string.sim_est_end).uppercase(), style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                        Text(stringResource(R.string.unit_gb, estimatedEnd), fontWeight = FontWeight.Black, fontSize = 20.sp, color = if (overBudget) Color.Red else colors.onSurface)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Nouvelle section Expert pour remplir l'espace
        Text(stringResource(R.string.sim_strat_advice), style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Black, color = colors.primary, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(16.dp))

        GlassCardSommite(colors = colors) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    if (overBudget) stringResource(R.string.sim_advice_risk) else stringResource(R.string.sim_advice_ok),
                    style = MaterialTheme.typography.bodySmall,
                    color = colors.onSurface.copy(alpha = 0.7f),
                    lineHeight = 20.sp
                )
                
                if (!overBudget) {
                    Spacer(modifier = Modifier.height(16.dp))
                    val suggestedBytes = (suggestedDaily * 1.5 * 1073741824).toLong()
                    val formattedUsage = io.arvo.dataconso.util.FormatUtils.formatDataSize(suggestedBytes, LocalContext.current)
                    Text(
                        stringResource(R.string.sim_tip_format, formattedUsage),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF10B981)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(40.dp)) // Plus d'espace en bas
    }
}

@Composable
private fun SimulationInputRow(label: String, value: Double, icon: ImageVector, colors: DataConsColors, isInteger: Boolean, onValueChange: (Double) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        Surface(
            modifier = Modifier.size(36.dp), 
            shape = CircleShape, 
            color = colors.primary.copy(alpha = 0.1f)
        ) {
            Icon(
                icon, null, 
                tint = colors.primary, 
                modifier = Modifier.padding(8.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label, 
            style = MaterialTheme.typography.bodyMedium, 
            fontWeight = FontWeight.Bold,
            color = colors.onSurface,
            modifier = Modifier.weight(1f)
        )
        
        OutlinedTextField(
            value = if (isInteger) value.toInt().toString() else String.format(Locale.US, "%.1f", value),
            onValueChange = { onValueChange(it.toDoubleOrNull() ?: value) },
            modifier = Modifier.width(90.dp),
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.End,
                color = colors.primary
            ),
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = colors.outline.copy(alpha = 0.3f),
                focusedBorderColor = colors.primary,
                cursorColor = colors.primary
            )
        )
    }
}

@Composable
private fun SimulationStatItem(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, fontWeight = FontWeight.Black, color = color)
    }
}
