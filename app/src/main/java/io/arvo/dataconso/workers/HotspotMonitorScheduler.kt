package io.arvo.dataconso.workers

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

object HotspotMonitorScheduler {
    private const val WORK_NAME = "arvo_hotspot_monitor"

    fun schedule(context: Context) {
        val request = PeriodicWorkRequestBuilder<HotspotMonitorWorker>(
            15,
            TimeUnit.MINUTES
        )
            .setInitialDelay(1, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(context.applicationContext).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
