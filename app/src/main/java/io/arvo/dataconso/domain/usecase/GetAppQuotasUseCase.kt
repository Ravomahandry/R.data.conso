package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.AppQuotaEntity
import io.arvo.dataconso.DataRepository
import io.arvo.dataconso.DataUsageManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import java.util.Calendar
import javax.inject.Inject

/**
 * UseCase spécialisé pour le calcul réactif de l'état des quotas applicatifs.
 * Sommité : Utilise une fusion directe des flux pour garantir la réactivité temps réel.
 */
class GetAppQuotasUseCase @Inject constructor(
    private val repository: DataRepository,
    private val usageManager: DataUsageManager,
    private val telephonyRepository: io.arvo.dataconso.TelephonyRepository,
    @dagger.hilt.android.qualifiers.ApplicationContext private val context: android.content.Context
) {
    operator fun invoke(rtAppWifi: Map<String, Long>, rtAppMobile: Map<String, Long>): Flow<List<AppQuotaEntity>> {
        val refreshTicker = flow {
            while (coroutineContext.isActive) {
                emit(Unit)
                delay(5000L)
            }
        }

        return combine(repository.quotasFlow, refreshTicker) { quotas, _ -> quotas }
            .map { quotas ->
            if (quotas.isEmpty()) return@map emptyList()

            val cal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
            }
            val startOfDay = cal.timeInMillis
            val now = System.currentTimeMillis()

            // Optimisation Rigueur : Une seule requête pour TOUTES les applications
            val wifiStats = usageManager.getMultiAppUsage(quotas.map { it.packageName }, startOfDay, now, android.net.ConnectivityManager.TYPE_WIFI)
            val mobileStats = usageManager.getMultiAppUsage(
                quotas.map { it.packageName },
                startOfDay,
                now,
                android.net.ConnectivityManager.TYPE_MOBILE,
                telephonyRepository.getDefaultDataSubId()
            )

            quotas.map { q ->
                val systemUsage = when(q.networkType) {
                    "WIFI" -> wifiStats[q.packageName]?.totalBytes ?: 0L
                    "MOBILE" -> mobileStats[q.packageName]?.totalBytes ?: 0L
                    else -> (wifiStats[q.packageName]?.totalBytes ?: 0L) + (mobileStats[q.packageName]?.totalBytes ?: 0L)
                }
                
                val realtimeUsage = when(q.networkType) {
                    "WIFI" -> rtAppWifi[q.packageName] ?: 0L
                    "MOBILE" -> rtAppMobile[q.packageName] ?: 0L
                    else -> (rtAppWifi[q.packageName] ?: 0L) + (rtAppMobile[q.packageName] ?: 0L)
                }

                // NetworkStats already includes the current session. The realtime
                // value is a fallback while the system counter catches up, not an
                // amount to add to it.
                val usedBytes = maxOf(systemUsage, realtimeUsage)
                q.copy(
                    usedBytes = usedBytes,
                    isBlocked = q.quotaBytes > 0 && usedBytes >= q.quotaBytes
                )
            }
        }
    }
}
