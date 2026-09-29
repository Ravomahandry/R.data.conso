package io.arvo.dataconso.domain.usecase

object VpnHealthCalculator {
    fun calculate(
        tunnelActive: Boolean,
        ruleCount: Int,
        errorCount: Int,
        rebuildCount: Int,
        tunnelUptimeMillis: Long
    ): Int {
        var score = if (tunnelActive) 40 else 0
        score += when {
            ruleCount == 0 -> 10
            ruleCount <= 10 -> 20
            ruleCount <= 50 -> 15
            else -> 10
        }
        score -= (errorCount.coerceAtLeast(0) * 15).coerceAtMost(45)
        score -= (rebuildCount.coerceAtLeast(0) * 5).coerceAtMost(25)
        score += when {
            !tunnelActive -> 0
            tunnelUptimeMillis >= STABLE_TUNNEL_MILLIS -> 20
            tunnelUptimeMillis >= 60_000L -> 10
            else -> 0
        }
        return score.coerceIn(0, 100)
    }

    private const val STABLE_TUNNEL_MILLIS = 30 * 60 * 1000L
}
