package io.arvo.dataconso.domain

import io.arvo.dataconso.AppSettings
import io.arvo.dataconso.DataRepository
import io.arvo.dataconso.DataUsageManager
import io.arvo.dataconso.NetworkSource
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetUsageUseCase @Inject constructor(
    private val repository: DataRepository
) {
    suspend operator fun invoke(source: NetworkSource, period: DataUsageManager.PeriodType): DataUsageManager.UsageBreakdown {
        val settings = repository.settingsFlow.first() ?: AppSettings()
        return repository.getUsage(source, period, settings)
    }
}
