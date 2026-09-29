package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.data.AppQuotaEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class VpnDiagnosticsTest {
    @Test
    fun runtimeStateReflectsProtectionTransitions() {
        assertEquals(
            VpnRuntimeState.ACTIVE,
            VpnDiagnosticsEngine.runtimeState(true, true, false, null)
        )
        assertEquals(
            VpnRuntimeState.REBUILDING,
            VpnDiagnosticsEngine.runtimeState(true, true, true, null)
        )
        assertEquals(
            VpnRuntimeState.PENDING,
            VpnDiagnosticsEngine.runtimeState(true, false, false, null)
        )
        assertEquals(
            VpnRuntimeState.ERROR,
            VpnDiagnosticsEngine.runtimeState(true, false, false, "Tunnel indisponible")
        )
        assertEquals(
            VpnRuntimeState.DISABLED,
            VpnDiagnosticsEngine.runtimeState(false, false, false, null)
        )
    }

    @Test
    fun diagnosticsExplainAppliedAndPendingQuotaRules() {
        val quotas = listOf(
            AppQuotaEntity(
                packageName = "com.example.blocked",
                appName = "Blocked app",
                quotaBytes = 100,
                usedBytes = 100
            ),
            AppQuotaEntity(
                packageName = "com.example.pending",
                appName = "Pending app",
                quotaBytes = 100,
                usedBytes = 150
            )
        )
        val report = VpnDiagnosticsEngine.evaluate(
            quotas,
            AppQuotaRuntimeSnapshot(
                appliedPackages = setOf("com.example.blocked"),
                pendingPackages = setOf("com.example.pending")
            ),
            VpnRuntimeSnapshot(tunnelActive = true)
        )

        assertEquals(AppQuotaDisplayStatus.BLOCKED, report.appDiagnostics[0].status)
        assertTrue(report.appDiagnostics[0].reason.contains("règles actives"))
        assertEquals(AppQuotaDisplayStatus.BLOCKING, report.appDiagnostics[1].status)
    }

    @Test
    fun healthScoreIsBoundedAndRewardsStableActiveTunnel() {
        val healthy = VpnHealthCalculator.calculate(
            tunnelActive = true,
            ruleCount = 3,
            errorCount = 0,
            rebuildCount = 0,
            tunnelUptimeMillis = 60 * 60 * 1000L
        )
        val unhealthy = VpnHealthCalculator.calculate(
            tunnelActive = false,
            ruleCount = 0,
            errorCount = 100,
            rebuildCount = 100,
            tunnelUptimeMillis = 0
        )

        assertEquals(80, healthy)
        assertEquals(0, unhealthy)
        assertTrue(healthy in 0..100)
        assertTrue(unhealthy in 0..100)
    }
}
