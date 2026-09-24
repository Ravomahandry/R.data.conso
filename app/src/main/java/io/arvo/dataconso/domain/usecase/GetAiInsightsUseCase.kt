package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.ArvoAiEngine
import io.arvo.dataconso.ArvoInsight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetAiInsightsUseCase @Inject constructor(
    private val aiEngine: ArvoAiEngine,
    private val repository: io.arvo.dataconso.DataRepository
) {
    operator fun invoke(): Flow<List<ArvoInsight>> = flow {
        try {
            val settings = repository.getSettings()
            val usage = repository.getUsageForBillingCycle(io.arvo.dataconso.NetworkSource.MOBILE, settings.billingCycleDay)
            val insights = aiEngine.generateInsights(usage.totalBytes)
            emit(insights)
        } catch (e: Exception) {
            emit(emptyList())
        }
    }.flowOn(Dispatchers.IO)
}
