package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.AppSettings
import io.arvo.dataconso.DataRepository
import io.arvo.dataconso.NetworkSource
import io.arvo.dataconso.DataUsageManager
import io.arvo.dataconso.domain.model.UsageData
import io.arvo.dataconso.QuotaCalculator
import javax.inject.Inject

/**
 * Cas d'usage pour la récupération atomique de l'usage.
 * Utilisé par le Dashboard et le système de monitoring.
 */
class GetUsageUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend fun execute(
        source: NetworkSource,
        settings: AppSettings
    ): UsageData {
        val usageToday = repository.getUsage(source, DataUsageManager.PeriodType.DAILY, settings)
        val usageMonth = repository.getUsageForBillingCycle(source, settings.billingCycleDay)
        
        val budget = when(source) { 
            NetworkSource.MOBILE -> settings.monthlyMobileGb
            NetworkSource.TOTAL -> settings.monthlyMobileGb + settings.monthlyWifiGb
            else -> settings.monthlyWifiGb 
        }
        
        val proj = QuotaCalculator.calculateProjection(budget, usageMonth.totalBytes / 1073741824.0, settings.billingCycleDay)
        val todayUsedGb = usageToday.totalBytes / 1073741824.0
        
        return UsageData(
            downloadBytes = usageToday.downloadBytes,
            uploadBytes = usageToday.uploadBytes,
            totalBytes = usageToday.totalBytes,
            dailyQuotaGb = proj.recommendedDailyGb,
            todayUsedGb = todayUsedGb,
            remainingGb = proj.remainingGb,
            progress = if (proj.recommendedDailyGb > 0) (todayUsedGb / proj.recommendedDailyGb).toFloat().coerceIn(0f, 1f) else 0f,
            isLimitExceeded = todayUsedGb > proj.recommendedDailyGb
        )
    }
}
