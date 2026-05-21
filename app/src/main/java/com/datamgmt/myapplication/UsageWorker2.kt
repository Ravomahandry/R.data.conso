package com.datamgmt.myapplication

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UsageWorker2(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val usageManager = DataUsageManager(context)
        val database = AppDatabase.getDatabase(context)
        val settingsDao = database.settingsDao()
        val historyDao = database.historyDao()

        // 1. Récupérer la consommation du jour
        val usedBytes = usageManager.getMobileUsageToday()
        val usedGb = usedBytes / (1024.0 * 1024.0 * 1024.0)

        // 2. Récupérer le quota configuré
        val settings = settingsDao.getSettings() ?: AppSettings()
        
        // Calculer le quota journalier théorique
        val calendar = Calendar.getInstance()
        val remainingDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) + 1
        val dailyQuotaGb = QuotaCalculator.calculateDailyQuota(
            settings.monthlyQuotaGb,
            0.0, // Simplification: on compare au quota mensuel divisé par jours restants
            remainingDays
        )

        // 3. Logique de blocage (squelette)
        val intent = Intent(context, VpnBlockService::class.java)
        if (usedGb >= dailyQuotaGb) {
            // Activer le VPN pour bloquer
            context.startService(intent)
        } else {
            // Arrêter le VPN pour débloquer
            context.stopService(intent)
        }

        // 4. Sauvegarder la conso actuelle en base pour l'UI
        settingsDao.saveSettings(settings.copy(
            currentUsageBytes = usedBytes,
            lastCheckTime = System.currentTimeMillis()
        ))

        // 5. Enregistrer historique journalier
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateLabel = sdf.format(System.currentTimeMillis())
        val entry = HistoryEntry(
            timestamp = System.currentTimeMillis(),
            dateLabel = dateLabel,
            simId = "TOTAL",
            bytes = usedBytes
        )
        historyDao.insert(entry)

        // 6. Notifications seuils 50/80/100% du quota mensuel
        val totalUsedBytes = settings.currentUsageBytes.coerceAtLeast(usedBytes)
        val quotaBytes = (settings.monthlyQuotaGb * 1024.0 * 1024.0 * 1024.0).toLong()
        if (quotaBytes > 0L) {
            val percent = (totalUsedBytes * 100) / quotaBytes
            val notifier = NotificationHelper(context)
            if (percent >= 100) notifier.notifyThreshold(100, settings.monthlyQuotaGb, totalUsedBytes)
            else if (percent >= 80) notifier.notifyThreshold(80, settings.monthlyQuotaGb, totalUsedBytes)
            else if (percent >= 50) notifier.notifyThreshold(50, settings.monthlyQuotaGb, totalUsedBytes)
        }

        return Result.success()
    }
}
