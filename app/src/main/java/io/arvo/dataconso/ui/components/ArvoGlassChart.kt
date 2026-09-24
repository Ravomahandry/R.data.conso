package io.arvo.dataconso.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.arvo.dataconso.MainViewModel
import io.arvo.dataconso.Granularity
import io.arvo.dataconso.HistoryEntry
import io.arvo.dataconso.NetworkSource
import io.arvo.dataconso.DataUsageManager
import io.arvo.dataconso.ui.DataConsColors
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ArvoGlassChart(
    history: List<HistoryEntry>,
    source: NetworkSource,
    granularity: Granularity,
    colors: DataConsColors, 
    modifier: Modifier = Modifier
) {
    val animatedProgress = remember { Animatable(0f) }
    val textMeasurer = rememberTextMeasurer()
    val labelColor = colors.graphLabel
    val gridColor = colors.gridLine
    val context = androidx.compose.ui.platform.LocalContext.current
    val formatter = remember { DataUsageManager(context) }
    val mainVm: MainViewModel = hiltViewModel()
    val settingsState = mainVm.settings.collectAsStateWithLifecycle()
    val isLowPerf = settingsState.value.lowPerformanceMode
    val locale = androidx.compose.ui.platform.LocalConfiguration.current.locales[0]

    val chartKey = remember(history.size, source, granularity) {
        // Sommité : Optimisation du calcul de clé pour éviter les recompositions inutiles
        "${history.size}_${source.name}_${granularity.name}"
    }

    LaunchedEffect(chartKey) {
        if (animatedProgress.value < 1f) {
            animatedProgress.animateTo(
                targetValue = 1f,
                animationSpec = if (isLowPerf) tween(0) else tween(durationMillis = 800, easing = FastOutSlowInEasing)
            )
        }
    }

    val displayData = remember(history, source, granularity, locale) {
        processHistoryForDisplay(history, granularity, locale)
    }

    val billingDay = settingsState.value.billingCycleDay
    val currentCycleStart = remember(billingDay) {
        Calendar.getInstance().apply {
            val now = Calendar.getInstance()
            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            if (now.get(Calendar.DAY_OF_MONTH) < billingDay) add(Calendar.MONTH, -1)
            set(Calendar.DAY_OF_MONTH, billingDay.coerceAtMost(getActualMaximum(Calendar.DAY_OF_MONTH)))
        }.timeInMillis
    }

    // Rigueur Sommité : On calcule le max SEULEMENT sur la source sélectionnée
    val maxVal = remember(displayData, source) {
        val rawMax = when(source) {
            NetworkSource.WIFI -> displayData.maxOfOrNull { it.wifi } ?: 1L
            NetworkSource.TOTAL -> displayData.maxOfOrNull { it.total } ?: 1L
            else -> displayData.maxOfOrNull { it.mobile } ?: 1L
        }
        rawMax.coerceAtLeast(1L).toFloat()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val leftPadding = 50.dp.toPx()
        val bottomPadding = 25.dp.toPx()
        val chartWidth = size.width - leftPadding
        val chartHeight = size.height - bottomPadding

        val barCount = displayData.size.coerceAtLeast(1)
        val spacingFactor = 0.35f
        val barWidth = (chartWidth / barCount) * (1f - spacingFactor)
        val spacing = (chartWidth / barCount) * spacingFactor

        val gridLines = 4
        for (i in 0..gridLines) {
            val y = chartHeight - (i * (chartHeight / gridLines))
            drawLine(color = gridColor, start = Offset(leftPadding, y), end = Offset(size.width, y), strokeWidth = 1.dp.toPx())
            val yVal = (maxVal / gridLines) * i
            val label = formatter.formatDataCompact(yVal.toLong())
            val textLayoutResult = textMeasurer.measure(text = label, style = TextStyle(color = labelColor, fontSize = 8.sp, fontWeight = FontWeight.Bold))
            drawText(textLayoutResult = textLayoutResult, topLeft = Offset(leftPadding - textLayoutResult.size.width - 8.dp.toPx(), y - textLayoutResult.size.height / 2))
        }

        displayData.forEachIndexed { index, data ->
            val x = leftPadding + index * (barWidth + spacing) + (spacing / 2)
            
            // Rigueur CEO : Opacité réduite pour les données hors cycle actuel
            val isCurrentCycle = data.timestamp >= currentCycleStart
            val isLastBar = index == displayData.size - 1
            val baseAlpha = if (isCurrentCycle) 1.0f else 0.4f
            
            // Sommité : Couleur spéciale pour la dernière barre (Aujourd'hui)
            val todayColor = Color(0xFFFACC15) // Or Sommité

            val currentVal = when(source) {
                NetworkSource.WIFI -> data.wifi
                NetworkSource.TOTAL -> data.total
                else -> data.mobile
            }

            if (source == NetworkSource.TOTAL) {
                val mH = (data.mobile.toFloat() / maxVal) * chartHeight * animatedProgress.value
                val wH = (data.wifi.toFloat() / maxVal) * chartHeight * animatedProgress.value
                
                val mColor = if (isLastBar) todayColor else colors.primary
                val wColor = if (isLastBar) todayColor.copy(alpha = 0.7f) else colors.accent
                
                drawBarPart(x, chartHeight - mH, barWidth, mH, mColor.copy(alpha = 0.8f * baseAlpha))
                drawBarPart(x, chartHeight - mH - wH, barWidth, wH, wColor.copy(alpha = 0.8f * baseAlpha))
            } else {
                val h = (currentVal.toFloat() / maxVal) * chartHeight * animatedProgress.value
                val barColor = if (isLastBar) todayColor else (if(source == NetworkSource.WIFI) colors.accent else colors.primary)
                drawBarPart(x, chartHeight - h, barWidth, h, barColor.copy(alpha = baseAlpha))
            }

            val labelStyle = TextStyle(
                color = if (isLastBar) todayColor else labelColor, 
                fontSize = 8.sp,
                fontWeight = if (isLastBar) FontWeight.Black else FontWeight.Normal
            )
            val xLabelLayout = textMeasurer.measure(text = data.label, style = labelStyle)
            drawText(textLayoutResult = xLabelLayout, topLeft = Offset(x + (barWidth / 2) - (xLabelLayout.size.width / 2), chartHeight + 4.dp.toPx()))

            if (currentVal > 0) {
                val valLabel = formatter.formatDataCompact(currentVal)
                val valColor = if (isLastBar) todayColor else (if(source == NetworkSource.WIFI) colors.accent else colors.primary)
                val valLabelLayout = textMeasurer.measure(text = valLabel, style = TextStyle(color = valColor.copy(alpha = 0.9f), fontSize = 7.sp, fontWeight = FontWeight.Black))
                val barHeight = (currentVal.toFloat() / maxVal) * chartHeight * animatedProgress.value
                if (barHeight > 15.dp.toPx()) {
                    drawText(textLayoutResult = valLabelLayout, topLeft = Offset(x + (barWidth / 2) - (valLabelLayout.size.width / 2), chartHeight - barHeight - valLabelLayout.size.height - 2.dp.toPx()))
                }
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBarPart(x: Float, y: Float, w: Float, h: Float, color: Color) {
    if (h <= 0) return
    val brush = Brush.verticalGradient(colors = listOf(color, color.copy(alpha = 0.3f)), startY = y, endY = y + h)
    drawRoundRect(brush = brush, topLeft = Offset(x, y), size = Size(w, h), cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx()))
}

private data class DisplayBarData(val label: String, val timestamp: Long, val mobile: Long, val wifi: Long) {
    val total: Long get() = mobile + wifi
}

private fun processHistoryForDisplay(history: List<HistoryEntry>, gran: Granularity, locale: Locale): List<DisplayBarData> {
    val result = mutableListOf<DisplayBarData>()
    val cal = Calendar.getInstance(locale)
    val storageSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val (count, field, displaySdf) = when (gran) {
        Granularity.DAILY -> Triple(7, Calendar.DAY_OF_YEAR, SimpleDateFormat("E", locale)) // 7 derniers jours avec abréviation professionnelle (Lun, Mar...)
        Granularity.WEEKLY -> Triple(6, Calendar.WEEK_OF_YEAR, SimpleDateFormat("'S'ww", locale))
        Granularity.MONTHLY -> Triple(6, Calendar.MONTH, SimpleDateFormat("MMM", locale))
    }
    cal.add(field, -(count - 1))
    repeat(count) {
        val label = displaySdf.format(cal.time)
        val targetDate = storageSdf.format(cal.time)
        val currentTime = cal.timeInMillis
        
        // Rigueur CEO : Filtrage Multi-SIM avec déduplication
        val dayRecords = history.filter { isMatchingDate(it, cal, gran, targetDate) }
        
        val wifi = dayRecords.filter { it.simId == "WIFI" }.sumOf { it.bytes }
        
        // Algorithme de priorité mobile
        val mobile = dayRecords.groupBy { it.dateLabel }.map { (_, records) ->
            val combined = records.find { it.simId == "SIM_COMBINED" }
            if (combined != null) {
                combined.bytes
            } else {
                records.filter { it.simId == "SIM_1" || it.simId == "SIM_2" }.sumOf { it.bytes }
            }
        }.sum()

        result.add(DisplayBarData(label, currentTime, mobile, wifi))
        cal.add(field, 1)
    }
    return result
}

private fun isMatchingDate(entry: HistoryEntry, cal: Calendar, gran: Granularity, targetDate: String): Boolean {
    return when (gran) {
        Granularity.DAILY -> entry.dateLabel == targetDate
        Granularity.WEEKLY -> {
            val hCal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            hCal.get(Calendar.WEEK_OF_YEAR) == cal.get(Calendar.WEEK_OF_YEAR) && hCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        }
        Granularity.MONTHLY -> {
            val hCal = Calendar.getInstance().apply { timeInMillis = entry.timestamp }
            hCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && hCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        }
    }
}
