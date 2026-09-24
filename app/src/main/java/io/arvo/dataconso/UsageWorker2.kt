package io.arvo.dataconso

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@HiltWorker
class UsageWorker2 @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val repository: DataRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val context = applicationContext
        val settings = repository.getSettings()
        
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        if (settings.lastResetDate != todayStr) {
            repository.saveSettings(settings.copy(lastResetDate = todayStr))
            // Sommité : Réinitialisation quotidienne des quotas d'applications
            repository.resetAllQuotas()
        }

        val timestamp = System.currentTimeMillis()

        // 1. Log WiFi
        val wifiUsage = repository.getUsage(NetworkSource.WIFI, DataUsageManager.PeriodType.DAILY, settings).totalBytes
        repository.saveHistoryEntry(HistoryEntry(timestamp = timestamp, dateLabel = todayStr, simId = "WIFI", bytes = wifiUsage))

        // 2. Log Mobile
        val mobileUsage = repository.getUsage(NetworkSource.MOBILE, DataUsageManager.PeriodType.DAILY, settings).totalBytes
        repository.saveHistoryEntry(HistoryEntry(timestamp = timestamp, dateLabel = todayStr, simId = "SIM_COMBINED", bytes = mobileUsage))

        // 4. Update Widget
        ArvoWidgetProvider.triggerUpdate(context)

        // 5. Watchdog : Redémarrer le service si nécessaire
        if (settings.vpnEnabled || settings.speedEnabled) {
            val isRunning = RealTimeData.isVpnRunning.value
            if (!isRunning) {
                val intent = Intent(context, VpnBlockService::class.java).apply { 
                    action = VpnBlockService.ACTION_REFRESH 
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        return Result.success()
    }
}

