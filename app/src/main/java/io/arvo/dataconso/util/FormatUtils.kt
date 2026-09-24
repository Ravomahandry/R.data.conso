package io.arvo.dataconso.util

import android.content.Context
import io.arvo.dataconso.R
import java.util.Locale

object FormatUtils {
    
    /**
     * Formats data size (bytes) into a localized string (e.g., "1.5 GB" or "1.5 Go").
     */
    fun formatDataSize(bytes: Long, context: Context): String {
        return when {
            bytes >= 1073741824 -> {  // >= 1 GB
                val gb = bytes / 1073741824.0
                val gbStr = String.format(Locale.getDefault(), "%.1f", gb)
                "$gbStr ${context.getString(R.string.data_unit_gb)}"
            }
            bytes >= 1048576 -> {     // >= 1 MB
                val mb = bytes / 1048576.0
                val mbStr = String.format(Locale.getDefault(), "%.0f", mb)
                "$mbStr ${context.getString(R.string.data_unit_mb)}"
            }
            else -> {                  // KB
                val kb = bytes / 1024.0
                val kbStr = String.format(Locale.getDefault(), "%.0f", kb)
                "$kbStr ${context.getString(R.string.data_unit_kb)}"
            }
        }
    }
    
    /**
     * Formats network speed (bytes per second) into a localized string (e.g., "1.5 Mbps").
     */
    fun formatSpeed(bytesPerSecond: Long, context: Context): String {
        return when {
            bytesPerSecond >= 1048576 -> {  // >= 1 Mbps
                val mbps = (bytesPerSecond * 8) / 1048576.0
                val mbpsStr = String.format(Locale.getDefault(), "%.1f", mbps)
                "$mbpsStr ${context.getString(R.string.speed_unit_mbps)}"
            }
            else -> {
                val kbps = (bytesPerSecond * 8) / 1024.0
                val kbpsStr = String.format(Locale.getDefault(), "%.1f", kbps)
                "$kbpsStr ${context.getString(R.string.speed_unit_kbps)}"
            }
        }
    }
}
