package io.arvo.dataconso.data

import android.net.ConnectivityManager
import io.arvo.dataconso.AppUsageInfo
import io.arvo.dataconso.DataUsageManager
import io.arvo.dataconso.Granularity
import io.arvo.dataconso.NetworkSource
import io.arvo.dataconso.QuotaCalculator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(
    private val database: AppDatabase,
    private val usageManager: DataUsageManager,
    private val settingsDataStore: SettingsDataStore
) {
    private val _settingsState = MutableStateFlow<AppSettings?>(null)
    val settingsState = _settingsState.asStateFlow()

    val settingsFlow: Flow<AppSettings> = database.settingsDao().getSettingsFlow()
        .map { it ?: AppSettings() }
        .distinctUntilChanged()
        .flowOn(Dispatchers.IO)

    val quotasFlow: Flow<List<AppQuotaEntity>> = database.quotaDao().getAllQuotasFlow()

    /**
     * Retrieves the current application settings from the local database.
     * Returns default settings if none are found.
     */
    suspend fun getSettings(): AppSettings = withContext(Dispatchers.IO) {
        database.settingsDao().getSettings() ?: AppSettings()
    }

    /**
     * Persists the provided settings to the local database.
     */
    suspend fun saveSettings(settings: AppSettings) = withContext(Dispatchers.IO) {
        database.settingsDao().saveSettings(settings)
    }

    private suspend fun ensureMigration() {
        if (!settingsDataStore.isMigrationCompleted()) {
            val roomSettings = database.settingsDao().getSettings()
            if (roomSettings != null) {
                settingsDataStore.updateSettings(roomSettings)
            }
            settingsDataStore.setMigrationCompleted()
        }
    }

    suspend fun getAllQuotas(): List<AppQuotaEntity> = withContext(Dispatchers.IO) {
        database.quotaDao().getAllQuotas()
    }

    suspend fun saveQuota(quota: AppQuotaEntity) = withContext(Dispatchers.IO) {
        database.quotaDao().saveQuota(quota)
    }

    suspend fun saveAllQuotas(quotas: List<AppQuotaEntity>) = withContext(Dispatchers.IO) {
        database.quotaDao().saveAllQuotas(quotas)
    }

    suspend fun resetAllQuotas() = withContext(Dispatchers.IO) {
        database.quotaDao().resetAllQuotas(System.currentTimeMillis())
    }

    suspend fun deleteQuota(quota: AppQuotaEntity) = withContext(Dispatchers.IO) {
        database.quotaDao().deleteQuota(quota)
    }

    suspend fun getAllHistory(): List<HistoryEntry> = withContext(Dispatchers.IO) {
        database.historyDao().getAll()
    }

    suspend fun saveHistoryEntry(entry: HistoryEntry) = withContext(Dispatchers.IO) {
        val oldEntry = database.historyDao().getEntryByDateAndSim(entry.dateLabel, entry.simId)
        database.historyDao().insert(entry)
        
        // Rigueur 4.1 : Calcul des économies fiabilisé
        if (entry.simId == "SIM_COMBINED") {
            val settings = getSettings()
            val dailyBudget = if (settings.dailyLimitGb > 0) settings.dailyLimitGb 
                              else QuotaCalculator.calculateIdealDaily(settings.monthlyMobileGb, settings.billingCycleDay)
            
            if (dailyBudget <= 0) return@withContext

            val usedGb = entry.bytes / 1073741824.0
            val oldUsedGb = (oldEntry?.bytes ?: 0L) / 1073741824.0
            
            // ✅ Calcul des économies RÉELLES (sans coefficient magique)
            val actualSavings = (dailyBudget - usedGb).coerceIn(0.0, dailyBudget)
            val oldSavings = (dailyBudget - oldUsedGb).coerceIn(0.0, dailyBudget)
            val deltaSavings = actualSavings - oldSavings
            
            val newTotalMoney = (settings.totalMoneySaved + deltaSavings).coerceAtLeast(0.0)
            
            var newTempPremium = settings.temporaryPremiumExpiry
            // Seuil de récompense documenté: 500 Mo économisés sur la journée
            if ((dailyBudget - usedGb) >= 0.5 && System.currentTimeMillis() > settings.temporaryPremiumExpiry) {
                newTempPremium = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
            }
            
            if (newTotalMoney != settings.totalMoneySaved || newTempPremium != settings.temporaryPremiumExpiry) {
                saveSettings(settings.copy(
                    totalMoneySaved = newTotalMoney,
                    temporaryPremiumExpiry = newTempPremium
                ))
            }
        }
    }

    suspend fun getTopApps(source: NetworkSource, gran: Granularity): List<AppUsageInfo> = withContext(Dispatchers.IO) {
        val cal = Calendar.getInstance()
        val endTime = cal.timeInMillis
        val startTime = when (gran) {
            Granularity.DAILY -> { cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0); cal.timeInMillis }
            Granularity.WEEKLY -> { cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0); cal.timeInMillis }
            Granularity.MONTHLY -> { cal.set(Calendar.DAY_OF_MONTH, 1); cal.set(Calendar.HOUR_OF_DAY, 0); cal.set(Calendar.MINUTE, 0); cal.set(Calendar.SECOND, 0); cal.set(Calendar.MILLISECOND, 0); cal.timeInMillis }
        }

        when (source) {
            NetworkSource.TOTAL -> {
                // Optimisation : Utilisation du cache UID partagé si possible, mais ici on combine simplement
                val wifi = usageManager.getTopAppsUsage(startTime, endTime, android.net.ConnectivityManager.TYPE_WIFI)
                val mobile = usageManager.getTopAppsUsage(startTime, endTime, android.net.ConnectivityManager.TYPE_MOBILE)
                (wifi + mobile).groupBy { it.packageName }
                    .map { (pkg, list) -> AppUsageInfo(pkg, list.first().appName, list.sumOf { it.bytes }) }
                    .sortedByDescending { it.bytes }
                    .take(5)
            }
            NetworkSource.WIFI -> usageManager.getTopAppsUsage(startTime, endTime, android.net.ConnectivityManager.TYPE_WIFI)
            NetworkSource.MOBILE -> usageManager.getTopAppsUsage(startTime, endTime, android.net.ConnectivityManager.TYPE_MOBILE)
        }
    }

    suspend fun getUsage(source: NetworkSource, period: DataUsageManager.PeriodType, settings: AppSettings): DataUsageManager.UsageBreakdown = withContext(Dispatchers.IO) {
        when (source) {
            NetworkSource.WIFI -> usageManager.getUsageBreakdownForPeriod(period, ConnectivityManager.TYPE_WIFI)
            NetworkSource.MOBILE -> usageManager.getUsageBreakdownForPeriod(period, ConnectivityManager.TYPE_MOBILE)
            else -> {
                val wifi = usageManager.getUsageBreakdownForPeriod(period, ConnectivityManager.TYPE_WIFI)
                val mobile = usageManager.getUsageBreakdownForPeriod(period, ConnectivityManager.TYPE_MOBILE)
                DataUsageManager.UsageBreakdown(wifi.downloadBytes + mobile.downloadBytes, wifi.uploadBytes + mobile.uploadBytes)
            }
        }
    }
    
    suspend fun getUsageForBillingCycle(source: NetworkSource, cycleDay: Int): DataUsageManager.UsageBreakdown = withContext(Dispatchers.IO) {
        when (source) {
            NetworkSource.WIFI -> usageManager.getUsageBreakdownForBillingCycle(cycleDay, ConnectivityManager.TYPE_WIFI)
            NetworkSource.MOBILE -> usageManager.getUsageBreakdownForBillingCycle(cycleDay, ConnectivityManager.TYPE_MOBILE)
            else -> {
                val wifi = usageManager.getUsageBreakdownForBillingCycle(cycleDay, ConnectivityManager.TYPE_WIFI)
                val mobile = usageManager.getUsageBreakdownForBillingCycle(cycleDay, ConnectivityManager.TYPE_MOBILE)
                DataUsageManager.UsageBreakdown(wifi.downloadBytes + mobile.downloadBytes, wifi.uploadBytes + mobile.uploadBytes)
            }
        }
    }
}
