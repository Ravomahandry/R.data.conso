package io.arvo.dataconso.domain

import java.net.NetworkInterface

interface HotspotStateDetector {
    fun isHotspotActive(): Boolean
}

class NetworkInterfaceHotspotStateDetector : HotspotStateDetector {
    override fun isHotspotActive(): Boolean {
        val interfaces = NetworkInterface.getNetworkInterfaces() ?: return false
        while (interfaces.hasMoreElements()) {
            val networkInterface = interfaces.nextElement()
            if (isHotspotInterfaceName(networkInterface.name) && networkInterface.isUp) {
                return true
            }
        }
        return false
    }

    internal fun isHotspotInterfaceName(name: String): Boolean =
        name.lowercase() in setOf("ap0", "softap0", "swlan0", "wifiap0") ||
            name.lowercase().matches(Regex("wlan\\d+_ap\\d*")) ||
            name.lowercase().matches(Regex("wlan[1-9]\\d*"))
}
