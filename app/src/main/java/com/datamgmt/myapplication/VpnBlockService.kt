package com.datamgmt.myapplication

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log

/**
 * VpnBlockService - skeleton VpnService prepared for future blocking.
 * It establishes a minimal interface and closes it on destroy.
 */
class VpnBlockService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            val builder = Builder()
            builder.setSession("DataBlockVPN")
                .addAddress("10.0.0.2", 24)
                .addRoute("0.0.0.0", 0)

            vpnInterface = builder.establish()
            Log.i("VpnBlockService", "VPN interface established (skeleton)")
        } catch (e: Exception) {
            Log.w("VpnBlockService", "Failed to establish VPN interface: ${e.message}")
        }

        return START_STICKY
    }

    override fun onDestroy() {
        try {
            vpnInterface?.close()
        } catch (ignored: Exception) {}
        super.onDestroy()
    }

    override fun onRevoke() {
        super.onRevoke()
        try {
            vpnInterface?.close()
        } catch (ignored: Exception) {}
    }
}