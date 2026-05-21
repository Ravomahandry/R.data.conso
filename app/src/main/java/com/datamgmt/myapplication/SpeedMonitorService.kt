package com.datamgmt.myapplication

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.TrafficStats
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.Locale
import java.util.Timer
import java.util.TimerTask

class SpeedMonitorService : Service() {

    private val channelId = "speed_monitor_channel"
    private val notificationId = 9999
    private var timer: Timer? = null
    private var lastRxBytes: Long = 0L
    private var lastTxBytes: Long = 0L
    private var lastTimestamp: Long = 0L

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        lastRxBytes = TrafficStats.getMobileRxBytes()
        lastTxBytes = TrafficStats.getMobileTxBytes()
        lastTimestamp = System.currentTimeMillis()

        startForeground(notificationId, buildNotification("⬇ 0 Ko/s", "⬆ 0 Ko/s"))

        timer?.cancel()
        timer = Timer()
        timer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                updateSpeed()
            }
        }, 1000, 1000)

        return START_STICKY
    }

    private fun updateSpeed() {
        val currentRx = TrafficStats.getMobileRxBytes()
        val currentTx = TrafficStats.getMobileTxBytes()
        val now = System.currentTimeMillis()
        val elapsed = (now - lastTimestamp) / 1000.0

        if (elapsed <= 0) return

        val rxSpeed = ((currentRx - lastRxBytes) / elapsed).toLong()
        val txSpeed = ((currentTx - lastTxBytes) / elapsed).toLong()

        lastRxBytes = currentRx
        lastTxBytes = currentTx
        lastTimestamp = now

        val downText = "⬇ ${formatSpeed(rxSpeed)}"
        val upText = "⬆ ${formatSpeed(txSpeed)}"

        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notificationId, buildNotification(downText, upText))
    }

    private fun formatSpeed(bytesPerSec: Long): String {
        val kb = 1024.0
        val mb = kb * 1024.0
        val d = bytesPerSec.toDouble()
        return when {
            d >= mb -> String.format(Locale.getDefault(), "%.1f Mo/s", d / mb)
            d >= kb -> String.format(Locale.getDefault(), "%.1f Ko/s", d / kb)
            else -> String.format(Locale.getDefault(), "%d o/s", bytesPerSec)
        }
    }

    private fun buildNotification(downSpeed: String, upSpeed: String): android.app.Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("$downSpeed    $upSpeed")
            .setContentText("Débit mobile en temps réel")
            .setSmallIcon(android.R.drawable.ic_menu_sort_by_size)
            .setContentIntent(pi)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setSilent(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Moniteur de débit",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Affiche le débit mobile en temps réel"
                setShowBadge(false)
            }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        timer?.cancel()
        timer = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
