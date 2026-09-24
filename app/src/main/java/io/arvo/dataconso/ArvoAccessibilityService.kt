package io.arvo.dataconso

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityWindowInfo
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Enforcer d'Accessibilité ARVO (Audit Etape 5.2).
 * Gère le blocage visuel immédiat basé sur les décisions du processus Core.
 */
@AndroidEntryPoint
class ArvoAccessibilityService : AccessibilityService() {

    @Inject lateinit var database: AppDatabase
    @Inject lateinit var repository: DataRepository
    
    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var lastPackage: String? = null
    
    private var lastBlockedTime = 0L
    private var lastBlockedPkg: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        val type = event.eventType
        
        if (type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED || type == AccessibilityEvent.TYPE_WINDOWS_CHANGED) {
            
            val packageName = event.packageName?.toString()
            
            // 1. Mise à jour de l'état de l'application au premier plan
            if (packageName != null && type == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                lastPackage = packageName
                
                // Si ce n'est pas ARVO, on notifie le service de monitoring
                if (packageName != "io.arvo.dataconso") {
                    sendBroadcast(Intent("io.arvo.dataconso.APP_CHANGED").apply { 
                        putExtra("pkg", packageName)
                        `package` = getPackageName()
                    })
                }
            }

            // 2. SÉCURITÉ : Si l'utilisateur est dans ARVO ou sur l'accueil, 
            // on ne force pas l'affichage de l'overlay pour les apps en arrière-plan.
            if (isSafePackage(lastPackage)) return

            // 3. Vérification du package de l'événement actuel
            if (packageName != null) {
                checkSecurityAndBlock(packageName, isEventPackage = true)
            }

            // 4. Scan des fenêtres flottantes / PiP
            try {
                windows.forEach { window ->
                    if (window.type == AccessibilityWindowInfo.TYPE_APPLICATION) {
                        val pkg = window.root?.packageName?.toString()
                        if (pkg != null && !isSafePackage(pkg)) {
                            checkSecurityAndBlock(pkg, isEventPackage = false)
                        }
                    }
                }
            } catch (e: Exception) { }
        }
    }

    private fun isSafePackage(pkg: String?): Boolean {
        if (pkg == null) return false
        return pkg == "io.arvo.dataconso" || 
               pkg == "com.android.settings" || 
               pkg == "com.android.systemui" ||
               pkg == "android" ||
               isLauncher(pkg)
    }

    private fun isLauncher(pkg: String): Boolean {
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
        val resolveInfo = packageManager.resolveActivity(intent, 0)
        return pkg == resolveInfo?.activityInfo?.packageName
    }

    private fun checkSecurityAndBlock(packageName: String, isEventPackage: Boolean) {
        if (packageName == "io.arvo.dataconso" || packageName == "com.android.settings" || packageName == "com.android.systemui") return

        serviceScope.launch {
            try {
                val settings = repository.getSettings()
                if (!settings.vpnEnabled && !settings.appFirewallEnabled) return@launch

                val quota = database.quotaDao().getQuotaForApp(packageName)
                val isAppBlocked = quota != null && quota.isEnabled && (quota.isBlocked || quota.isManualBlocked)

                if (isAppBlocked) {
                    val now = System.currentTimeMillis()
                    if (packageName == lastBlockedPkg && now - lastBlockedTime < 3000) return@launch
                    lastBlockedTime = now
                    lastBlockedPkg = packageName
                    
                    Log.w("ArvoA11y", "🚫 App Quota Reached: $packageName. VPN blocking is active.")
                    
                    // Rigueur Sommité : Nous ne lançons plus l'overlay visuel.
                    // Le blocage est assuré techniquement par le VpnBlockService qui capture le trafic de ce package.
                }
            } catch (e: Exception) {
                Log.e("ArvoA11y", "Error in block check", e)
            }
        }
    }

    override fun onInterrupt() {}
}
