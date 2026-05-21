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

        // 1. Récupérer la consommation du jour et du mois
        val todayBytes = usageManager.getMobileUsageToday()
        val todayGb = todayBytes / (1024.0 * 1024.0 * 1024.0)
        val monthBytes = usageManager.getMobileUsageThisMonth()
        val monthGb = monthBytes / (1024.0 * 1024.0 * 1024.0)

        // 2. Récupérer le quota configuré
        val settings = settingsDao.getSettings() ?: AppSettings()

        // Calculer le quota journalier avec report automatique :
        // quota journalier = (quota mensuel - conso du mois AVANT aujourd'hui) / jours restants
        // Si non consommé les jours précédents, le surplus est réparti sur les jours restants
        val calendar = Calendar.getInstance()
        val remainingDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) + 1
        val usedBeforeToday = monthGb - todayGb
        val dailyQuotaGb = QuotaCalculator.calculateDailyQuota(
            settings.monthlyQuotaGb,
            usedBeforeToday.coerceAtLeast(0.0),
            remainingDays
        )

        // 3. Logique de blocage VPN : si quota journalier atteint, bloquer immédiatement
        val intent = Intent(context, VpnBlockService::class.java)
        if (dailyQuotaGb > 0 && todayGb >= dailyQuotaGb) {
            context.startService(intent)
        } else {
            context.stopService(intent)
        }

        // 4. Sauvegarder la conso actuelle en base pour l'UI
        settingsDao.saveSettings(settings.copy(
            currentUsageBytes = monthBytes,
            lastCheckTime = System.currentTimeMillis()
        ))

        // 5. Enregistrer historique journalier
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateLabel = sdf.format(System.currentTimeMillis())
        val entry = HistoryEntry(
            timestamp = System.currentTimeMillis(),
            dateLabel = dateLabel,
            simId = "TOTAL",
            bytes = todayBytes
        )
        historyDao.insert(entry)

        // 6. Notifications seuils 50/80/100% du quota mensuel
        val quotaBytes = (settings.monthlyQuotaGb * 1024.0 * 1024.0 * 1024.0).toLong()
        if (quotaBytes > 0L) {
            val percent = (monthBytes * 100) / quotaBytes
            val notifier = NotificationHelper(context)
            if (percent >= 100) notifier.notifyThreshold(100, settings.monthlyQuotaGb, monthBytes)
            else if (percent >= 80) notifier.notifyThreshold(80, settings.monthlyQuotaGb, monthBytes)
            else if (percent >= 50) notifier.notifyThreshold(50, settings.monthlyQuotaGb, monthBytes)
        }

        return Result.success()
    }
}
