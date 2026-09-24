package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.AppSettings
import io.arvo.dataconso.ArvoInsight
import io.arvo.dataconso.InsightType
import io.arvo.dataconso.domain.model.UsageData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

/**
 * UseCase destiné à l'intégration de Gemini Nano (AICore) ou logique locale avancée.
 * Pour ARVO V2, il génère une stratégie d'économie basée sur la projection actuelle.
 */
class GenerateAiStrategyUseCase @Inject constructor() {
    operator fun invoke(usageData: UsageData, settings: AppSettings): Flow<ArvoInsight?> = flow {
        if (usageData.isLimitExceeded) {
            emit(ArvoInsight(
                title = "Stratégie d'Urgence Active",
                description = "Vous dépassez votre quota quotidien. ARVO recommande d'activer le blocage DNS des publicités pour économiser ~15% de data.",
                type = InsightType.WARNING,
                priority = 10
            ))
        } else if (usageData.progress > 0.8) {
            emit(ArvoInsight(
                title = "Alerte de Rythme",
                description = "À ce rythme, vous épuiserez votre forfait 4 jours avant la fin du cycle. Réduisez la qualité vidéo sur YouTube.",
                type = InsightType.SUGGESTION,
                priority = 5
            ))
        } else {
            emit(null)
        }
    }
}
