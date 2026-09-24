package io.arvo.dataconso.security

import android.content.Context
import java.io.File

/**
 * Sommité Security Suite (Phase 5)
 * Root detection and Integrity checks.
 */
object SecurityUtils {

    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su", "/system/usr/we-need-root/su-backup",
            "/system/xbin/mu"
        )
        try {
            for (path in paths) { if (File(path).exists()) return true }
        } catch (_: Exception) {}
        
        // Check for su in PATH
        return try {
            Runtime.getRuntime().exec("which su").inputStream.bufferedReader().readLine() != null
        } catch (_: Exception) { false }
    }

    fun checkIntegrity(context: Context): Boolean {
        // Rigueur : Un environnement sain n'est PAS debuggable ET n'est PAS rooté
        val isDebuggable = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
        return !isDebuggable && !isDeviceRooted()
    }

    fun isLowEndDevice(context: Context): Boolean {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager
        val memoryInfo = android.app.ActivityManager.MemoryInfo()
        activityManager.getMemoryInfo(memoryInfo)
        
        // Seuil : Moins de 4 Go de RAM ou processeur avec moins de 4 coeurs
        val totalRamGb = memoryInfo.totalMem / (1024 * 1024 * 1024)
        val cores = Runtime.getRuntime().availableProcessors()
        
        return totalRamGb < 4 || cores < 4 || activityManager.isLowRamDevice
    }
}
