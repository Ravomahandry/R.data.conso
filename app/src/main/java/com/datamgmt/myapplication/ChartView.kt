package com.datamgmt.myapplication

import android.view.ViewGroup
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun UsageLineChart(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    var entries by remember { mutableStateOf(listOf<Entry>()) }

    // Load sample data asynchronously from DB
    LaunchedEffect(Unit) {
        val db = AppDatabase.getDatabase(context)
        val hist = db.historyDao().getAll()
        val list = hist.reversed().mapIndexed { idx, it -> Entry(idx.toFloat(), it.bytes.toFloat() / (1024f * 1024f)) }
        entries = list
    }

    AndroidView(factory = { ctx ->
        LineChart(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            description = Description().apply { text = "Consommation (Mo)" }
            setNoDataText("Pas de données")
        }
    }, update = { chart ->
        val ds = LineDataSet(entries, "Consommation journalière")
        ds.color = android.graphics.Color.RED
        ds.valueTextColor = android.graphics.Color.WHITE
        val ld = LineData(ds)
        chart.data = ld
        chart.invalidate()
    }, modifier = modifier)
}
