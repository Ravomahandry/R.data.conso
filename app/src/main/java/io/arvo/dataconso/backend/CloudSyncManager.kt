package io.arvo.dataconso.backend

import android.util.Log
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import io.arvo.dataconso.data.AppSettings
import io.arvo.dataconso.data.DataRepository
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
    private val analytics: FirebaseAnalytics
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
