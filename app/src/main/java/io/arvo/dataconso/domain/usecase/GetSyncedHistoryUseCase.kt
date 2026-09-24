package io.arvo.dataconso.domain.usecase

import io.arvo.dataconso.DataRepository
import io.arvo.dataconso.HistoryEntry
import io.arvo.dataconso.NetworkSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * UseCase pour récupérer l'historique synchronisé avec les données Live d'aujourd'hui.
 */
class GetSyncedHistoryUseCase @Inject constructor(
    private val repository: DataRepository
) {
    operator fun invoke(liveToday: Map<NetworkSource, Long>): Flow<List<HistoryEntry>> = flow {
        val history = repository.getAllHistory().toMutableList()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val todayStr = sdf.format(Date())
        val now = System.currentTimeMillis()

        val sourcesToSync = listOf(
            "WIFI" to (liveToday[NetworkSource.WIFI] ?: 0L),
            "SIM_COMBINED" to (liveToday[NetworkSource.MOBILE] ?: 0L)
        )

        sourcesToSync.forEach { (id, bytes) ->
            val idx = history.indexOfFirst { it.dateLabel == todayStr && it.simId == id }
            if (idx != -1) {
                history[idx] = history[idx].copy(bytes = bytes, timestamp = now)
            } else if (bytes > 0) {
                history.add(HistoryEntry(timestamp = now, dateLabel = todayStr, simId = id, bytes = bytes))
            }
        }

        emit(history.sortedByDescending { it.timestamp })
    }
}
