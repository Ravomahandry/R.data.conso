package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.data.AppQuotaEntity

enum class AppQuotaDisplayStatus {
    MONITORING,
    BLOCKED,
    BLOCKING,
    PENDING,
    ERROR
}

data class AppQuotaRuntimeSnapshot(
    val globalBlocked: Boolean = false,
    val appliedPackages: Set<String> = emptySet(),
    val pendingPackages: Set<String> = emptySet(),
    val failedPackages: Set<String> = emptySet(),
    val errorMessage: String? = null,
    val measurementError: String? = null
)

object AppQuotaPolicy {
    fun combineUsage(networkType: String, wifiBytes: Long, mobileBytes: Long): Long {
        val wifi = wifiBytes.coerceAtLeast(0)
        val mobile = mobileBytes.coerceAtLeast(0)
        return when (networkType) {
            "WIFI" -> wifi
            "MOBILE" -> mobile
            else -> saturatedAdd(wifi, mobile)
        }
    }

    fun isQuotaExceeded(quota: AppQuotaEntity, usedBytes: Long): Boolean =
        quota.isEnabled && quota.quotaBytes > 0 && usedBytes >= quota.quotaBytes

    fun shouldBlock(quota: AppQuotaEntity, usedBytes: Long): Boolean =
        quota.isEnabled && (quota.isManualBlocked || isQuotaExceeded(quota, usedBytes))

    fun displayStatus(
        quota: AppQuotaEntity,
        runtime: AppQuotaRuntimeSnapshot,
        tunnelActive: Boolean
    ): AppQuotaDisplayStatus {
        if (!shouldBlock(quota, quota.usedBytes)) {
            return if (quota.isEnabled && runtime.measurementError != null) {
                AppQuotaDisplayStatus.ERROR
            } else {
                AppQuotaDisplayStatus.MONITORING
            }
        }
        return when {
            quota.packageName in runtime.failedPackages -> AppQuotaDisplayStatus.ERROR
            runtime.globalBlocked && tunnelActive -> AppQuotaDisplayStatus.BLOCKED
            quota.packageName in runtime.appliedPackages && tunnelActive -> AppQuotaDisplayStatus.BLOCKED
            quota.packageName in runtime.pendingPackages &&
                runtime.errorMessage == null && runtime.measurementError == null ->
                AppQuotaDisplayStatus.BLOCKING
            runtime.errorMessage != null || runtime.measurementError != null -> AppQuotaDisplayStatus.ERROR
            else -> AppQuotaDisplayStatus.PENDING
        }
    }

    private fun saturatedAdd(left: Long, right: Long): Long =
        if (Long.MAX_VALUE - left < right) Long.MAX_VALUE else left + right
}
