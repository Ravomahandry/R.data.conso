package io.arvo.dataconso

import android.os.SystemClock
import java.util.Calendar

object QuotaCalculator {

    private const val MILLIS_PER_DAY = 24.0 * 60.0 * 60.0 * 1000.0

    private fun normalizedBillingDay(billingCycleDay: Int): Int = billingCycleDay.coerceIn(1, 28)

    private fun buildCycleStart(
        now: Calendar,
        billingCycleDay: Int
    ): Calendar {
        val normalized = normalizedBillingDay(billingCycleDay)
        return (now.clone() as Calendar).apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (get(Calendar.DAY_OF_MONTH) < normalized) {
                add(Calendar.MONTH, -1)
            }
            set(Calendar.DAY_OF_MONTH, normalized.coerceAtMost(getActualMaximum(Calendar.DAY_OF_MONTH)))
        }
    }

    private fun buildNextCycleStart(
        now: Calendar,
        billingCycleDay: Int
    ): Calendar {
        val nextCycle = buildCycleStart(now, billingCycleDay)
        nextCycle.add(Calendar.MONTH, 1)
        nextCycle.set(Calendar.DAY_OF_MONTH, normalizedBillingDay(billingCycleDay).coerceAtMost(nextCycle.getActualMaximum(Calendar.DAY_OF_MONTH)))
        return nextCycle
    }

    fun calculateBillingCycleLengthDays(
        billingCycleDay: Int,
        now: Calendar = Calendar.getInstance()
    ): Int {
        val cycleStart = buildCycleStart(now, billingCycleDay)
        val nextCycleStart = buildNextCycleStart(now, billingCycleDay)
        val millisDiff = nextCycleStart.timeInMillis - cycleStart.timeInMillis
        return kotlin.math.ceil(millisDiff / MILLIS_PER_DAY).toInt().coerceAtLeast(1)
    }

    /**
     * Sommité : Calcul des jours restants sécurisé.
     */
    fun calculateDaysRemaining(
        billingCycleDay: Int,
        now: Calendar = Calendar.getInstance()
    ): Int {
        val nextCycle = buildNextCycleStart(now, billingCycleDay)
        val millisDiff = nextCycle.timeInMillis - now.timeInMillis
        val fullDays = kotlin.math.ceil(millisDiff / MILLIS_PER_DAY).toInt()
        return fullDays.coerceAtLeast(1)
    }

    fun calculateDailyQuota(
        monthlyQuotaGb: Double,
        usedGb: Double,
        remainingDays: Int
    ): Double {
        if (remainingDays <= 0) return 0.0
        val remainingData = (monthlyQuotaGb - usedGb).coerceAtLeast(0.0)
        return remainingData / remainingDays
    }

    /**
     * Calcule le budget quotidien "théorique" basé sur le forfait mensuel total.
     */
    fun calculateIdealDaily(monthlyQuotaGb: Double, billingCycleDay: Int): Double {
        val now = Calendar.getInstance()
        val cycleLengthDays = calculateBillingCycleLengthDays(billingCycleDay, now)
        return if (cycleLengthDays > 0) monthlyQuotaGb / cycleLengthDays else 0.0
    }

    /**
     * Calcule le surplus accumulé depuis le début du cycle de facturation.
     * Sommité : C'est le "Cœur" de la logique ARVO.
     */
    fun calculateAccumulatedSurplus(
        monthlyBudgetGb: Double,
        usedThisMonthGb: Double,
        billingCycleDay: Int
    ): Double {
        val now = Calendar.getInstance()
        val cycleStart = buildCycleStart(now, billingCycleDay)
        val daysElapsed = ((now.timeInMillis - cycleStart.timeInMillis) / MILLIS_PER_DAY).toInt()
        
        // On ne compte pas aujourd'hui dans le surplus, seulement les jours révolus
        val dailyAllowance = calculateIdealDaily(monthlyBudgetGb, billingCycleDay)
        val expectedUsedUntilYesterday = dailyAllowance * daysElapsed
        
        // L'usage total ce mois-ci inclut aujourd'hui. 
        // Pour le surplus réel, on compare ce qu'on devrait avoir consommé jusqu'à hier.
        // Mais par simplicité et rigueur, on calcule la "Réserve" actuelle.
        val theoreticalUsedSoFar = dailyAllowance * (daysElapsed + 1)
        return (theoreticalUsedSoFar - usedThisMonthGb).coerceAtLeast(0.0)
    }

    /**
     * Phase 3 : Projection IA renforcée par le temps monotone et le cumul.
     */
    fun calculateProjection(
        monthlyBudgetGb: Double,
        usedThisMonthGb: Double,
        billingCycleDay: Int
    ): ProjectionResult {
        val daysRemaining = calculateDaysRemaining(billingCycleDay).coerceAtLeast(1)
        val remainingBudget = (monthlyBudgetGb - usedThisMonthGb).coerceAtLeast(0.0)
        
        // Logique Subdivision Sommité : Disponible pour aujourd'hui = (Idéal) + (Cumul non consommé)
        val idealDaily = calculateIdealDaily(monthlyBudgetGb, billingCycleDay)
        val surplus = calculateAccumulatedSurplus(monthlyBudgetGb, usedThisMonthGb, billingCycleDay)
        
        // La limite du jour est donc flexible : elle autorise l'usage du jour + tout ce qui a été économisé.
        // Rigueur : On ne cap pas à 0 pour permettre au service de détecter le dépassement.
        val flexibleDailyLimit = if (remainingBudget <= 0) 0.0 else (idealDaily + surplus).coerceAtMost(remainingBudget)

        val isOverBudget = usedThisMonthGb >= monthlyBudgetGb

        return ProjectionResult(
            recommendedDailyGb = flexibleDailyLimit,
            daysRemaining = daysRemaining,
            statusRes = if (isOverBudget) R.string.status_overconsumption else R.string.status_on_track,
            remainingGb = remainingBudget
        )
    }
}
