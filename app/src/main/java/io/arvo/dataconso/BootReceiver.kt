package io.arvo.dataconso

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject lateinit var repository: DataRepository

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Intent.ACTION_BOOT_COMPLETED) return

        val appContext = context.applicationContext
        val pendingResult = goAsync()
        
        // S'assurer que le scheduler est actif dès le boot
        WorkScheduler.schedulePeriodicWork(appContext)

        CoroutineScope(Dispatchers.IO + SupervisorJob()).launch {
            try {
                val settings = repository.getSettings()

                if (settings.vpnEnabled || settings.speedEnabled) {
                    val vpnIntent = Intent(appContext, VpnBlockService::class.java).apply {
                        action = VpnBlockService.ACTION_REFRESH
                    }
                    startService(appContext, vpnIntent)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun startService(context: Context, intent: Intent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }
}
