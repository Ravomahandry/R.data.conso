package com.datamgmt.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class NotificationHelper(private val context: Context) {

    private val channelId = "r_datacons_alerts"
    private val channelName = "Alerts"

    init {
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            ch.description = "Notifications sur seuils de consommation"
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
    }

    fun notifyThreshold(percent: Int, quotaGb: Double, usedBytes: Long) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val title = when (percent) {
            50 -> "Consommation 50%"
            80 -> "Consommation 80%"
            100 -> "Consommation 100%"
            else -> "Consommation"
        }
        val used = DataUsageManager.humanReadable(usedBytes)
        val text = "Quota: ${String.format("%.1f Go", quotaGb)} — Utilisé: $used ($percent%)"

        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, percent, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val n = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentIntent(pi)
            .setAutoCancel(true)
            .build()

        nm.notify(percent, n)
    }
}
