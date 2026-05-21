package com.datamgmt.myapplication

import android.content.Context
import android.content.Intent
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import java.util.Calendar

class UsageWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val usageManager = DataUsageManager(context)
        val database = AppDatabase.getDatabase(context)
        val settingsDao = database.settingsDao()

        // 1. Récupérer la consommation du jour et du mois
        val todayBytes = usageManager.getMobileUsageToday()
        val todayGb = todayBytes / (1024.0 * 1024.0 * 1024.0)
        val monthBytes = usageManager.getMobileUsageThisMonth()
        val monthGb = monthBytes / (1024.0 * 1024.0 * 1024.0)

        // 2. Récupérer le quota configuré
        val settings = settingsDao.getSettings() ?: AppSettings()

        // Calculer le quota journalier avec report automatique :
        // quota journalier = (quota mensuel - conso du mois AVANT aujourd'hui) / jours restants
        val calendar = Calendar.getInstance()
        val remainingDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH) - calendar.get(Calendar.DAY_OF_MONTH) + 1
        val usedBeforeToday = monthGb - todayGb
        val dailyQuotaGb = QuotaCalculator.calculateDailyQuota(
            settings.monthlyQuotaGb,
            usedBeforeToday.coerceAtLeast(0.0),
            remainingDays
        )

        // 3. Logique de blocage VPN : si quota journalier atteint, bloquer
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

        return Result.success()
    }
}