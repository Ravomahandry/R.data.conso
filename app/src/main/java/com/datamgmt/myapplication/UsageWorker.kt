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

        // 3. Logique de blocage
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

        return Result.success()
    }
}