package io.arvo.dataconso

import android.app.Application
import android.util.Log
import dagger.hilt.android.HiltAndroidApp
import androidx.hilt.work.HiltWorkerFactory
import javax.inject.Inject
import androidx.work.Configuration
import androidx.work.WorkManager

import android.app.ActivityManager
import android.content.IntentFilter
import android.os.Build
import com.google.firebase.FirebaseApp

@HiltAndroidApp
class ArvoApplication : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun onCreate() {
        super.onCreate()

        WorkManager.initialize(this, workManagerConfiguration)
        
        // Rigueur Etape 10 : Application de la locale globale
        val prefs = getSharedPreferences("arvo_settings", android.content.Context.MODE_PRIVATE)
        val lang = prefs.getString("selected_language", "fr") ?: "fr"
        val appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(lang)
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale)
        
        // Sommité : Initialisation Firebase robuste
        // On vérifie si Firebase n'est pas déjà initialisé pour éviter les crashes multi-processus
        try {
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this)
                Log.d("ARVO_APP", "Firebase initialized successfully")
            }
        } catch (e: Exception) {
            Log.e("ARVO_APP", "Firebase initialization failed", e)
        }

        // Sommité : Synchronisation Multi-processus des données temps réel
        if (isMainProcess()) {
            val filter = IntentFilter("io.arvo.dataconso.SYNC_REALTIME")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                registerReceiver(RealTimeData.SyncReceiver(), filter, RECEIVER_NOT_EXPORTED)
            } else {
                registerReceiver(RealTimeData.SyncReceiver(), filter)
            }
        }

        // Sommité : Chargement des bibliothèques SQLCipher avant tout accès BDD
        // Rigueur : Avec la version 4.17.0+, le chargement se fait via System.loadLibrary
        try {
            System.loadLibrary("sqlcipher")
            Log.d("ARVO_APP", "SQLCipher native libraries loaded successfully")
        } catch (e: Throwable) {
            Log.e("ARVO_APP", "Failed to load SQLCipher native library", e)
        }
    }

    private fun isMainProcess(): Boolean {
        val am = getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val processes = am.runningAppProcesses
        if (processes != null) {
            for (process in processes) {
                if (process.pid == android.os.Process.myPid()) {
                    return process.processName == packageName
                }
            }
        }
        return true
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder().apply {
            if (::workerFactory.isInitialized) {
                setWorkerFactory(workerFactory)
            }
        }.build()
}
