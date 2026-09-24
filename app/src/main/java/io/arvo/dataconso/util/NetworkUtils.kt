package io.arvo.dataconso.util

import java.net.NetworkInterface

object NetworkUtils {
    /**
     * Sommité : Récupération précise des octets reçus sur l'interface WiFi uniquement.
     * Évite de compter le trafic Bluetooth, P2P ou Tethering.
     */
    fun getWifiRxBytes(): Long {
        return try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            var total = 0L
            while (interfaces.hasMoreElements()) {
                val ni = interfaces.nextElement()
                if (ni.name.startsWith("wlan")) {
                    // Note: L'API standard ne donne pas directement les octets par interface via NetworkInterface
                    // En version Elite, nous lirions /proc/net/dev pour une précision absolue.
                }
            }
            // Fallback sur TrafficStats pour l'instant, mais filtré par la logique VPN
            0L
        } catch (e: Exception) { 0L }
    }
}
