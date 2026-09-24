package io.arvo.dataconso.network

import android.os.ParcelFileDescriptor
import android.util.Log
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Intercepteur de paquets ARVO - Phase Expert (NDK v2).
 * Utilise des flags atomiques natifs pour une latence proche de zéro.
 */
@Singleton
class RealPacketInterceptor @Inject constructor() {
    
    companion object {
        init {
            try {
                System.loadLibrary("arvo_native")
            } catch (e: Throwable) {
                Log.e("ARVO_NATIVE", "Failed to load native library", e)
            }
        }
    }

    private var interceptionJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var currentGlobal = false


    fun updateBlockingState(global: Boolean) {
        currentGlobal = global

        try {
            updateNativeState(global)
        } catch (_: Throwable) {}
    }

    fun startInterception(pfd: ParcelFileDescriptor) {
        stopInterception()
        interceptionJob = scope.launch {
            try {
                runNativePacketLoop(pfd.fd, currentGlobal)
            } catch (e: Exception) {
                Log.e("ARVO_NET_NATIVE", "Native Engine Crash", e)
            }
        }
    }

    fun stopInterception() {
        try {
            stopNativeLoop()
        } catch (_: Throwable) {}
        interceptionJob?.cancel()
        interceptionJob = null
    }

    // --- JNI Bridge ---
    
    private external fun updateNativeState(global: Boolean)
    private external fun stopNativeLoop()
    private external fun runNativePacketLoop(fd: Int, global: Boolean)
}
