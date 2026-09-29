package io.arvo.dataconso.backend

import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.arvo.dataconso.data.AppSettings
import io.arvo.dataconso.data.DataRepository
import io.arvo.dataconso.data.HotspotSessionEntity
import io.arvo.dataconso.repository.HotspotRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gestionnaire de Synchronisation Cloud ARVO (Phase 4).
 * Centralise la sauvegarde des économies et de l'état Premium sur Firebase.
 * Sommité : Ajout de l'auto-sync et du logging Analytics.
 */
@Singleton
class CloudSyncManager @Inject constructor(
    private val repository: DataRepository,
    private val analytics: FirebaseAnalytics,
    private val hotspotRepository: HotspotRepository
) {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val auth = FirebaseAuth.getInstance()
    private val db = try {
        FirebaseDatabase.getInstance().reference
    } catch (_: Exception) {
        null
    }

    init {
        // Rigueur Sommité : Désactivation temporaire de l'auto-sync pour éviter tout crash au démarrage si Firebase n'est pas configuré
    }

    fun syncNow() {
        scope.launch {
            try {
                // Tentative de connexion anonyme si non connecté
                var user = auth.currentUser
                if (user == null) {
                    try {
                        val result = auth.signInAnonymously().await()
                        user = result.user
                        logEvent("auth_anonymous_success", null)
                    } catch (e: Exception) {
                        Log.e("ARVO_CLOUD", "Anonymous auth failed", e)
                        return@launch
                    }
                }
                
                if (user == null) return@launch

                restoreHotspotSessions()
                val settings = repository.getSettings()
                val data = mapOf(
                    "totalMoneySaved" to settings.totalMoneySaved,
                    "isPremium" to settings.isPremium,
                    "vpnEnabled" to settings.vpnEnabled,
                    "lastSync" to System.currentTimeMillis(),
                    "deviceInfo" to android.os.Build.MODEL,
                    "osVersion" to android.os.Build.VERSION.RELEASE
                )
                
                val database = db
                if (database != null) {
                    database.child("users").child(user.uid).updateChildren(data).await()
                    Log.d("ARVO_CLOUD", "Sync successful for user: ${user.uid}")
                }
                syncHotspotSessions()
                
                // Analytics : Suivi des économies par palier
                if (settings.totalMoneySaved > 0) {
                    val bundle = android.os.Bundle().apply {
                        putDouble("amount", settings.totalMoneySaved)
                    }
                    analytics.logEvent("sync_savings", bundle)
                }
            } catch (e: Exception) {
                Log.e("ARVO_CLOUD", "Sync failed", e)
            }
        }
    }

    fun logEvent(name: String, params: android.os.Bundle?) {
        analytics.logEvent(name, params)
    }

    suspend fun syncHotspotSessions() {
        val user = auth.currentUser ?: return
        val database = db ?: return
        val sessionsRef = database.child("users").child(user.uid).child("hotspotSessions")
        for (session in hotspotRepository.getUnsyncedClosedSessions()) {
            val key = "${session.startTimestamp}_${session.sessionId}"
            sessionsRef.child(key).setValue(
                mapOf(
                    "startTimestamp" to session.startTimestamp,
                    "endTimestamp" to session.endTimestamp,
                    "durationMillis" to session.durationMillis,
                    "rxBytes" to session.rxBytes,
                    "txBytes" to session.txBytes,
                    "totalBytes" to session.totalBytes,
                    "sessionId" to session.sessionId
                )
            ).await()
            hotspotRepository.markSynced(session.id)
        }
    }

    suspend fun restoreHotspotSessions() {
        val user = auth.currentUser ?: return
        try {
            val database = db ?: return
            val snapshot = database.child("users")
                .child(user.uid)
                .child("hotspotSessions")
                .get()
                .await()

            for (child in snapshot.children) {
                val startTimestamp = child.child("startTimestamp").getValue(Long::class.java) ?: continue
                val sessionId = child.child("sessionId").getValue(String::class.java) ?: continue
                val endTimestamp = child.child("endTimestamp").getValue(Long::class.java) ?: continue
                if (startTimestamp <= 0 || endTimestamp < startTimestamp) continue

                hotspotRepository.restoreSession(
                    HotspotSessionEntity(
                        startTimestamp = startTimestamp,
                        endTimestamp = endTimestamp,
                        durationMillis = child.child("durationMillis").getValue(Long::class.java)
                            ?: endTimestamp - startTimestamp,
                        rxBytes = child.child("rxBytes").getValue(Long::class.java) ?: 0,
                        txBytes = child.child("txBytes").getValue(Long::class.java) ?: 0,
                        totalBytes = child.child("totalBytes").getValue(Long::class.java) ?: 0,
                        synced = true,
                        sessionId = sessionId
                    )
                )
            }
        } catch (exception: Exception) {
            Log.e("ARVO_CLOUD", "Hotspot session restore failed", exception)
        }
    }

    /**
     * Tente de restaurer les données depuis le cloud après une réinstallation.
     */
    suspend fun restoreFromCloud() {
        val user = auth.currentUser ?: return
        try {
            val database = db ?: return
            val snapshot = database.child("users").child(user.uid).get().await()
            if (snapshot.exists()) {
                val cloudSavings = snapshot.child("totalMoneySaved").getValue(Double::class.java) ?: 0.0
                val cloudPremium = snapshot.child("isPremium").getValue(Boolean::class.java) ?: false
                
                val currentSettings = repository.getSettings()
                repository.saveSettings(currentSettings.copy(
                    totalMoneySaved = maxOf(currentSettings.totalMoneySaved, cloudSavings),
                    isPremium = currentSettings.isPremium || cloudPremium
                ))
                Log.d("ARVO_CLOUD", "Restore successful. Savings: $cloudSavings")
            }
        } catch (e: Exception) {
            Log.e("ARVO_CLOUD", "Restore failed", e)
        }
    }
}
