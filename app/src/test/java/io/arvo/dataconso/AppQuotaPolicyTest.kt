package io.arvo.dataconso

import io.arvo.dataconso.data.AppQuotaEntity
import io.arvo.dataconso.domain.usecase.AppQuotaDisplayStatus
import io.arvo.dataconso.domain.usecase.AppQuotaPolicy
import io.arvo.dataconso.domain.usecase.AppQuotaRuntimeSnapshot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppQuotaPolicyTest {
    private val quota = AppQuotaEntity(
        packageName = "com.example.video",
        appName = "Video",
        quotaBytes = 500,
        usedBytes = 500
    )

    @Test
    fun quotaBoundaryAndDisabledQuotaUseTheSameEnforcementRule() {
        assertTrue(AppQuotaPolicy.isQuotaExceeded(quota, 500))
        assertTrue(AppQuotaPolicy.shouldBlock(quota, 500))
        assertFalse(AppQuotaPolicy.shouldBlock(quota.copy(isEnabled = false, isManualBlocked = true), 900))
    }

    @Test
    fun combinesBothNetworksWithoutOverflow() {
        assertEquals(Long.MAX_VALUE, AppQuotaPolicy.combineUsage("BOTH", Long.MAX_VALUE, 4))
        assertEquals(90L, AppQuotaPolicy.combineUsage("WIFI", 90, 40))
        assertEquals(40L, AppQuotaPolicy.combineUsage("MOBILE", 90, 40))
    }

    @Test
    fun distinguishesMeasuredBlockedFromRequestedOrFailedBlock() {
        assertEquals(
            AppQuotaDisplayStatus.BLOCKED,
            AppQuotaPolicy.displayStatus(
                quota,
                AppQuotaRuntimeSnapshot(appliedPackages = setOf(quota.packageName)),
                tunnelActive = true
            )
        )
        assertEquals(
            AppQuotaDisplayStatus.BLOCKING,
            AppQuotaPolicy.displayStatus(
                quota,
                AppQuotaRuntimeSnapshot(pendingPackages = setOf(quota.packageName)),
                tunnelActive = false
            )
        )
        assertEquals(
            AppQuotaDisplayStatus.PENDING,
            AppQuotaPolicy.displayStatus(quota, AppQuotaRuntimeSnapshot(), tunnelActive = false)
        )
        assertEquals(
            AppQuotaDisplayStatus.ERROR,
            AppQuotaPolicy.displayStatus(
                quota,
                AppQuotaRuntimeSnapshot(failedPackages = setOf(quota.packageName)),
                tunnelActive = false
            )
        )
    }
}
