package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.data.AppQuotaEntity

data class AppVpnDiagnostic(
    val packageName: String,
    val status: AppQuotaDisplayStatus,
    val reason: String
)

data class VpnDiagnosticsReport(
    val runtime: VpnRuntimeSnapshot,
    val appDiagnostics: List<AppVpnDiagnostic>
)

object VpnDiagnosticsEngine {
    fun runtimeState(
        blockRequired: Boolean,
        tunnelActive: Boolean,
        rebuilding: Boolean,
        errorMessage: String?
    ): VpnRuntimeState = when {
        errorMessage != null -> VpnRuntimeState.ERROR
        rebuilding -> VpnRuntimeState.REBUILDING
        blockRequired && tunnelActive -> VpnRuntimeState.ACTIVE
        blockRequired -> VpnRuntimeState.PENDING
        else -> VpnRuntimeState.DISABLED
    }

    fun evaluate(
        quotas: List<AppQuotaEntity>,
        runtime: AppQuotaRuntimeSnapshot,
        vpn: VpnRuntimeSnapshot
    ): VpnDiagnosticsReport {
        val appDiagnostics = quotas.map { quota ->
            val status = AppQuotaPolicy.displayStatus(quota, runtime, vpn.tunnelActive)
            val reason = when (status) {
                AppQuotaDisplayStatus.BLOCKED -> when {
                    runtime.globalBlocked -> "Le quota global est appliqué par le tunnel VPN."
                    quota.isManualBlocked -> "Le blocage manuel est appliqué par le tunnel VPN."
                    else -> "Le quota est atteint et l'application est incluse dans les règles actives du tunnel."
                }
                AppQuotaDisplayStatus.BLOCKING ->
                    "Le seuil est atteint ; le service prépare ou reconstruit le tunnel VPN."
                AppQuotaDisplayStatus.PENDING -> when {
                    runtime.measurementError != null -> runtime.measurementError
                    runtime.errorMessage != null -> runtime.errorMessage
                    else -> "Le seuil est atteint, mais ARVO ne confirme pas encore l'application de la règle réseau."
                }
                AppQuotaDisplayStatus.ERROR -> when {
                    quota.packageName in runtime.failedPackages ->
                        runtime.errorMessage ?: "La règle de cette application a échoué."
                    runtime.measurementError != null -> runtime.measurementError
                    else -> runtime.errorMessage ?: "Le blocage réseau n'a pas pu être confirmé."
                }
                AppQuotaDisplayStatus.MONITORING -> when {
                    !quota.isEnabled -> "Le suivi ou la règle de quota de cette application est désactivé."
                    quota.quotaBytes <= 0 -> "Aucun seuil positif n'est configuré."
                    else -> "La consommation mesurée reste sous le seuil configuré."
                }
            }
            AppVpnDiagnostic(quota.packageName, status, reason)
        }
        return VpnDiagnosticsReport(vpn, appDiagnostics)
    }
}
