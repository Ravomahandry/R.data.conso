package io.arvo.dataconso

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.components.Legend
import android.view.ViewGroup
import io.arvo.dataconso.util.FormatUtils
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DataBarChart(
    history: List<HistoryEntry>,
    predictions: List<Double> = emptyList(),
    source: NetworkSource,
    selectedGranularity: Granularity,
    textColor: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    val mobileColor = remember { android.graphics.Color.parseColor("#10204D") }
    val wifiColor = remember { android.graphics.Color.parseColor("#2E7D32") }
    val predictionColor = remember { android.graphics.Color.parseColor("#6366F1") }
    val chartTextColor = remember(textColor) {
        android.graphics.Color.argb(
            (textColor.alpha * 255).toInt(),
            (textColor.red * 255).toInt(),
            (textColor.green * 255).toInt(),
            (textColor.blue * 255).toInt()
        )
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val buildResult = remember(history, predictions, source, selectedGranularity) {
        buildChartData(context, history, predictions, source, selectedGranularity, mobileColor, wifiColor, predictionColor)
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            BarChart(context).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                description.isEnabled = false
                setNoDataText("Prise en compte des données...")
                legend.textColor = chartTextColor
                legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
                legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    this.textColor = chartTextColor
                    granularity = 1f
                    setAvoidFirstLastClipping(true)
                }
                axisLeft.apply {
                    setDrawGridLines(true)
                    this.textColor = chartTextColor
                    axisMinimum = 0f
                    valueFormatter = object : ValueFormatter() {
                        override fun getFormattedValue(value: Float): String {
                            val bytes = (value * 1048576).toLong()
                            return FormatUtils.formatDataSize(bytes, context)
                        }
                    }
                }
                axisRight.isEnabled = false
                setScaleEnabled(true)
                setPinchZoom(true)
                setFitBars(true)
                animateY(1000)
            }
        },
        update = { chart ->
            chart.setNoDataTextColor(chartTextColor)
            chart.data = buildResult.data
            chart.xAxis.textColor = chartTextColor
            chart.axisLeft.textColor = chartTextColor
            chart.legend.textColor = chartTextColor
            
            chart.xAxis.valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return buildResult.labels.getOrNull(value.toInt()) ?: ""
                }
            }
            
            buildResult.data?.let { barData ->
                barData.setValueTextColor(chartTextColor)
                barData.setValueTextSize(8f)
                barData.setValueFormatter(object : ValueFormatter() {
                    override fun getFormattedValue(value: Float): String {
                        val bytes = (value * 1048576).toLong()
                        return if (value >= 1) FormatUtils.formatDataSize(bytes, context) else ""
                    }
                })
            }

            chart.xAxis.axisMinimum = -0.5f
            chart.xAxis.axisMaximum = if (buildResult.labels.isNotEmpty()) buildResult.labels.size - 0.5f else 0.5f
            
            // Adjust zoom based on granularity
            val visibleCount = when(selectedGranularity) {
                Granularity.DAILY -> 7f
                Granularity.WEEKLY -> 8f
                Granularity.MONTHLY -> 6f
            }
            chart.setVisibleXRangeMaximum(visibleCount)
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    )
}

data class ChartBuildResult(val data: BarData?, val labels: List<String>)

private fun buildChartData(
    context: android.content.Context, 
    history: List<HistoryEntry>, 
    predictions: List<Double>,
    source: NetworkSource, 
    gran: Granularity, 
    mColor: Int, 
    wColor: Int,
    pColor: Int
): ChartBuildResult {
    fun bytesToMb(b: Long) = b.toFloat() / (1024f * 1024f)

    val mobileSeries = processHistory(history.filter { it.simId == "SIM_COMBINED" }, gran)
    val wifiSeries = processHistory(history.filter { it.simId == "WIFI" }, gran)
    
    val labels = mutableListOf<String>()
    val entries = mutableListOf<BarEntry>()
    val colorsList = mutableListOf<Int>()

    // 1. Ajouter l'historique réel
    val historyLabels = if (wifiSeries.size > mobileSeries.size) wifiSeries.map { it.first } else mobileSeries.map { it.first }
    historyLabels.indices.forEach { i ->
        labels.add(historyLabels[i])
        val m = bytesToMb(mobileSeries.getOrNull(i)?.second ?: 0L)
        val w = bytesToMb(wifiSeries.getOrNull(i)?.second ?: 0L)
        
        when (source) {
            NetworkSource.TOTAL -> {
                entries.add(BarEntry(i.toFloat(), floatArrayOf(m, w)))
                // Note: Colors will be handled by the DataSet for stacks
            }
            NetworkSource.WIFI -> {
                entries.add(BarEntry(i.toFloat(), w))
                colorsList.add(wColor)
            }
            NetworkSource.MOBILE -> {
                entries.add(BarEntry(i.toFloat(), m))
                colorsList.add(mColor)
            }
        }
    }

    // 2. Ajouter les prédictions IA (Seulement pour le mode jour)
    if (predictions.isNotEmpty() && gran == Granularity.DAILY) {
        val startIdx = labels.size
        predictions.forEachIndexed { i, valMo ->
            labels.add("IA ${i+1}")
            entries.add(BarEntry((startIdx + i).toFloat(), valMo.toFloat()))
            colorsList.add(pColor)
        }
    }

    if (labels.isEmpty()) return ChartBuildResult(null, emptyList())

    val set = BarDataSet(entries, context.getString(source.labelRes)).apply {
        if (source == NetworkSource.TOTAL && predictions.isEmpty()) {
            colors = listOf(mColor, wColor)
            stackLabels = arrayOf(context.getString(R.string.mobile_label), context.getString(R.string.wifi_label))
        } else {
            colors = colorsList
        }
        setDrawValues(true)
    }

    return ChartBuildResult(BarData(set), labels)
}

private fun processHistory(history: List<HistoryEntry>, gran: Granularity): List<Pair<String, Long>> {
    val result = mutableListOf<Pair<String, Long>>()
    val cal = Calendar.getInstance()
    val storageSdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    val (count, field, displaySdf) = when (gran) {
        Granularity.DAILY -> Triple(30, Calendar.DAY_OF_YEAR, SimpleDateFormat("dd/MM", Locale.getDefault()))
        Granularity.WEEKLY -> Triple(12, Calendar.WEEK_OF_YEAR, SimpleDateFormat("'S'ww", Locale.getDefault()))
        Granularity.MONTHLY -> Triple(12, Calendar.MONTH, SimpleDateFormat("MMM yy", Locale.getDefault()))
    }

    cal.add(field, -(count - 1))
    repeat(count) {
        val label = displaySdf.format(cal.time)
        val targetDate = storageSdf.format(cal.time)

        val finalVal = when (gran) {
            Granularity.DAILY -> history.firstOrNull { it.dateLabel == targetDate }?.bytes ?: 0L
            Granularity.WEEKLY -> {
                val targetWeek = cal.get(Calendar.WEEK_OF_YEAR)
                val targetYear = cal.get(Calendar.YEAR)
                history.filter {
                    val hCal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    hCal.get(Calendar.WEEK_OF_YEAR) == targetWeek && hCal.get(Calendar.YEAR) == targetYear
                }.sumOf { it.bytes }
            }
            Granularity.MONTHLY -> {
                val targetMonth = cal.get(Calendar.MONTH)
                val targetYear = cal.get(Calendar.YEAR)
                history.filter {
                    val hCal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
                    hCal.get(Calendar.MONTH) == targetMonth && hCal.get(Calendar.YEAR) == targetYear
                }.sumOf { it.bytes }
            }
        }

        result.add(label to finalVal)
        cal.add(field, 1)
    }
    return result
}
