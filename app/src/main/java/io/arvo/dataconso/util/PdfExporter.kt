package io.arvo.dataconso.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.util.Log
import io.arvo.dataconso.HistoryEntry
import io.arvo.dataconso.MainViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object PdfExporter {
    fun generateUsageReport(context: Context, state: MainViewModel.UiState): String? {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = document.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint()

        // Header
        paint.color = Color.rgb(16, 32, 77) // ARVO Blue
        paint.textSize = 24f
        canvas.drawText("ARVO 2.0 - Bilan de Consommation", 50f, 50f, paint)

        paint.textSize = 12f
        paint.color = Color.BLACK
        val dateStr = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        canvas.drawText("Généré le : $dateStr", 50f, 80f, paint)

        // Summary
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Résumé du Cycle", 50f, 120f, paint)
        
        paint.isFakeBoldText = false
        paint.textSize = 12f
        canvas.drawText("Usage ce mois : ${FormatUtils.formatDataSize((state.monthUsedGb * 1073741824).toLong(), context)}", 50f, 150f, paint)
        canvas.drawText("Economies réalisées : ${FormatUtils.formatDataSize((state.surplusGb * 1073741824).toLong(), context)}", 50f, 170f, paint)
        canvas.drawText("Limite quotidienne suggérée : ${FormatUtils.formatDataSize((state.dailyQuotaGb * 1073741824).toLong(), context)}", 50f, 190f, paint)

        // Top Apps
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Top Applications (Aujourd'hui)", 50f, 240f, paint)

        paint.isFakeBoldText = false
        paint.textSize = 12f
        var y = 270f
        state.topApps.forEach { app ->
            canvas.drawText("${app.appName} : ${FormatUtils.formatDataSize(app.bytes, context)}", 70f, y, paint)
            y += 20f
        }

        // History Table
        paint.textSize = 16f
        paint.isFakeBoldText = true
        canvas.drawText("Historique Récent", 50f, y + 30f, paint)
        
        y += 60f
        paint.textSize = 10f
        paint.isFakeBoldText = false
        canvas.drawText("Date", 50f, y, paint)
        canvas.drawText("Réseau", 150f, y, paint)
        canvas.drawText("Consommation", 300f, y, paint)
        
        canvas.drawLine(50f, y + 5f, 500f, y + 5f, paint)
        y += 20f

        state.history.take(15).forEach { entry ->
            if (y > 780f) return@forEach // Basic overflow protection
            canvas.drawText(entry.dateLabel, 50f, y, paint)
            canvas.drawText(entry.simId, 150f, y, paint)
            canvas.drawText(FormatUtils.formatDataSize(entry.bytes, context), 300f, y, paint)
            y += 15f
        }

        document.finishPage(page)

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "ARVO_Report_${System.currentTimeMillis()}.pdf")
        return try {
            document.writeTo(FileOutputStream(file))
            document.close()
            file.absolutePath
        } catch (e: IOException) {
            Log.e("PdfExporter", "Failed to write PDF", e)
            document.close()
            null
        } catch (e: Exception) {
            Log.e("PdfExporter", "Unexpected error", e)
            document.close()
            null
        }
    }
}
