package io.arvo.dataconso

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import java.util.Locale

class NotificationHelper(private val context: Context) {

    private val channelId = "arvo_alerts_v1"
    private val channelName = "ARVO Smart Alerts"
    
    // Simple deduplication cache: Percent -> Day string
    private val firedMap = mutableMapOf<Int, String>()

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            ch.description = "Notifications de dépassement de quota (50%, 80%, 100%)"
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
    }

    fun notifyThreshold(percent: Int, label: String, usedBytes: Long) {
        val today = java.text.SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(java.util.Date())
        
        // Prevent double fire for the same threshold on the same day
        if (firedMap[percent] == today) return
        
        val title = context.getString(R.string.threshold_alert_title)
        val text = context.getString(R.string.threshold_alert_msg, percent, label)

        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, percent, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val n = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher) // Changement icône pour cohérence ARVO
            .setContentIntent(pi)
            .setAutoCancel(true)
            .setColor(0xFF2563EB.toInt()) // Bleu ARVO
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }
        
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(percent + 1000, n)
        firedMap[percent] = today
    }
}
