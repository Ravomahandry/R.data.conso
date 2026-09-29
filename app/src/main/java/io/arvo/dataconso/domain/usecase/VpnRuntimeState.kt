package io.arvo.dataconso.domain.usecase

enum class VpnRuntimeState {
    ACTIVE,
    REBUILDING,
    ERROR,
    PENDING,
    DISABLED
}

data class VpnRuntimeSnapshot(
    val state: VpnRuntimeState = VpnRuntimeState.DISABLED,
    val tunnelActive: Boolean = false,
    val monitoredApps: Int = 0,
    val blockedApps: Int = 0,
    val lastSyncTimestamp: Long? = null,
    val tunnelSinceTimestamp: Long? = null,
    val rebuildCount: Int = 0,
    val errorCount: Int = 0,
    val errorMessage: String? = null
)

enum class VpnDiagnosticEventType {
    TUNNEL_REBUILD,
    BLOCK_STARTED,
    BLOCK_APPLIED,
    BLOCK_RELEASED,
    ERROR
}

data class VpnDiagnosticEvent(
    val id: Long,
    val timestamp: Long,
    val type: VpnDiagnosticEventType,
    val packageName: String? = null,
    val message: String
)
