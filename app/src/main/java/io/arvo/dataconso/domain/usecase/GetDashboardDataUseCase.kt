package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.Granularity
import io.arvo.dataconso.NetworkSource
import io.arvo.dataconso.DataRepository
import io.arvo.dataconso.domain.model.DashboardData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

class GetDashboardDataUseCase @Inject constructor(
    private val repository: DataRepository,
    private val getUsageUseCase: GetUsageUseCase
) {
    operator fun invoke(
        source: NetworkSource,
        granularity: Granularity
    ): Flow<DashboardData> = combine(
        repository.settingsFlow,
        repository.quotasFlow
    ) { settings, _ ->
        val s = settings ?: io.arvo.dataconso.AppSettings()
        
        val usage = getUsageUseCase.execute(source, s)
        val history = repository.getAllHistory()
        val topApps = repository.getTopApps(source, granularity)
        
        DashboardData(
            usage = usage,
            history = history,
            topApps = topApps
        )
    }
}
