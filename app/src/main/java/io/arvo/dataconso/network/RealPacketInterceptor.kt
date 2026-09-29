package io.arvo.dataconso.network

import android.os.ParcelFileDescriptor
import android.util.Log
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Intercepteur de paquets ARVO - Phase Expert (NDK v2).
 * Utilise des flags atomiques natifs pour une latence proche de zéro.
 */
@Singleton
class RealPacketInterceptor @Inject constructor() {
    enum class Status {
        IDLE,
        STARTING,
        ACTIVE,
        ERROR
    }

    data class State(val status: Status = Status.IDLE, val errorMessage: String? = null)

    
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
    private val _state = MutableStateFlow(State())
    val state = _state.asStateFlow()

    private var currentGlobal = false


    fun updateBlockingState(global: Boolean) {
        currentGlobal = global

        try {
            updateNativeState(global)
        } catch (_: Throwable) {}
    }

    fun startInterception(pfd: ParcelFileDescriptor) {
        stopInterception()
        _state.value = State(Status.STARTING)
        interceptionJob = scope.launch {
            try {
                val generation = beginNativeLoop()
                _state.value = State(Status.ACTIVE)
                runNativePacketLoop(pfd.fd, currentGlobal, generation)
                if (isActive) {
                    _state.value = State(Status.ERROR, "Packet interception stopped unexpectedly")
                }
            } catch (e: Exception) {
                Log.e("ARVO_NET_NATIVE", "Native Engine Crash", e)
                if (isActive) _state.value = State(Status.ERROR, e.message ?: e.javaClass.simpleName)
            } catch (e: LinkageError) {
                Log.e("ARVO_NET_NATIVE", "Native Engine Linkage Failure", e)
                if (isActive) _state.value = State(Status.ERROR, e.message ?: e.javaClass.simpleName)
            }
        }
    }

    fun stopInterception() {
        try {
            stopNativeLoop()
        } catch (_: Throwable) {}
        interceptionJob?.cancel()
        interceptionJob = null
        _state.value = State()
    }

    // --- JNI Bridge ---
    
    private external fun updateNativeState(global: Boolean)
    private external fun beginNativeLoop(): Int
    private external fun stopNativeLoop()
    private external fun runNativePacketLoop(fd: Int, global: Boolean, generation: Int)
}
