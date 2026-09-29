package io.arvo.dataconso.workers

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.arvo.dataconso.backend.CloudSyncManager
import io.arvo.dataconso.domain.HotspotStateDetector
import io.arvo.dataconso.repository.HotspotRepository

@HiltWorker
class HotspotMonitorWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters,
    private val detector: HotspotStateDetector,
    private val hotspotRepository: HotspotRepository,
    private val cloudSyncManager: CloudSyncManager
) : CoroutineWorker(context, parameters) {

    override suspend fun doWork(): Result {
        return try {
            if (detector.isHotspotActive()) {
                hotspotRepository.startSession()
                hotspotRepository.refreshActiveSession()
            } else {
                hotspotRepository.stopSession()
            }

            try {
                cloudSyncManager.syncHotspotSessions()
            } catch (exception: Exception) {
                Log.e(TAG, "Unable to sync hotspot sessions; local history is retained", exception)
            }
            Result.success()
        } catch (exception: Exception) {
            Log.e(TAG, "Hotspot monitoring failed", exception)
            Result.retry()
        }
    }

    private companion object {
        const val TAG = "HotspotMonitorWorker"
    }
}
