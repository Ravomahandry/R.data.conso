package io.arvo.dataconso.security

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import com.google.android.play.core.integrity.IntegrityManagerFactory
import com.google.android.play.core.integrity.IntegrityTokenRequest
import com.google.android.play.core.integrity.IntegrityTokenResponse
import io.arvo.dataconso.BuildConfig
import io.arvo.dataconso.util.TimeUtils
import kotlinx.coroutines.tasks.await
import java.io.File
import java.security.MessageDigest
import java.util.UUID

/**
 * Gardien de Sécurité ARVO 2.0 (Phase 4).
 * Intègre Play Integrity API pour une protection de grade production.
 */
object AppSecurityGuard {
    private const val TAG = "ARVO_SEC"
    
    // Hash de signature de production (À configurer lors de la publication)
    private const val PROD_SIGNATURE_HASH = "VOTRE_HASH_ICI" 

    /**
     * Vérification composite de la sécurité de l'environnement.
     */
    fun isEnvironmentSafe(context: Context): Boolean {
        if (isDebuggable(context)) return true // Autorise le debug local
        
        val isRooted = isDeviceRooted()
        val isEmulator = isRunningOnEmulator()
        val isTampered = !verifySignature(context)
        
        return !isRooted && !isEmulator && !isTampered
    }

    /**
     * Demande un jeton d'intégrité à Google Play Services.
     * @param requestNonce Un identifiant unique (UUID) pour prévenir les attaques par rejeu.
     */
    suspend fun getIntegrityToken(context: Context, requestNonce: String): String? {
        return try {
            val integrityManager = IntegrityManagerFactory.create(context.applicationContext)
            
            // On prépare la requête avec le nonce sécurisé
            val projectNumber = 0L
            val request = IntegrityTokenRequest.builder()
                .setCloudProjectNumber(projectNumber)
                .setNonce(requestNonce)
                .build()

            val response: IntegrityTokenResponse = integrityManager.requestIntegrityToken(request).await()
            response.token()
        } catch (e: Exception) {
            Log.e(TAG, "Play Integrity Request Failed", e)
            null
        }
    }

    /**
     * Génère un Nonce sécurisé basé sur le temps monotone et un UUID.
     */
    fun generateSecureNonce(): String {
        val raw = "${UUID.randomUUID()}-${TimeUtils.getMonotonicTime()}"
        return android.util.Base64.encodeToString(raw.toByteArray(), android.util.Base64.URL_SAFE or android.util.Base64.NO_WRAP)
    }

    private fun isRunningOnEmulator(): Boolean {
        return (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
                || Build.FINGERPRINT.startsWith("generic")
                || Build.FINGERPRINT.startsWith("unknown")
                || Build.HARDWARE.contains("goldfish")
                || Build.HARDWARE.contains("ranchu")
                || Build.MODEL.contains("google_sdk")
                || Build.MODEL.contains("Emulator")
                || Build.MODEL.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || Build.PRODUCT.contains("sdk_google")
                || Build.PRODUCT.contains("google_sdk")
                || Build.PRODUCT.contains("sdk")
                || Build.PRODUCT.contains("sdk_x86")
                || Build.PRODUCT.contains("vbox86p")
                || Build.PRODUCT.contains("emulator")
                || Build.PRODUCT.contains("simulator")
    }

    fun isDeviceRooted(): Boolean {
        val paths = arrayOf(
            "/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su",
            "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
            "/system/bin/failsafe/su", "/data/local/su", "/system/xbin/mu"
        )
        try {
            for (path in paths) { if (File(path).exists()) return true }
        } catch (_: Exception) {}
        return try {
            Runtime.getRuntime().exec("which su").inputStream.bufferedReader().readLine() != null
        } catch (_: Exception) { false }
    }

    @SuppressLint("PackageManagerGetSignatures")
    fun verifySignature(context: Context): Boolean {
        if (isDebuggable(context)) return true

        if (!isProductionSignatureConfigured()) {
            Log.w(TAG, "Production signature hash is not configured; rejecting release build.")
            return false
        }

        val currentHash = getSignatureHash(context) ?: return false

        return currentHash == PROD_SIGNATURE_HASH
    }

    fun isProductionSignatureConfigured(): Boolean {
        return PROD_SIGNATURE_HASH.isNotBlank() && PROD_SIGNATURE_HASH != "VOTRE_HASH_ICI"
    }

    private fun getSignatureHash(context: Context): String? {
        return try {
            val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNING_CERTIFICATES)
            } else {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            }
            
            val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.signingInfo?.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageInfo.signatures
            }

            signatures?.firstOrNull()?.let { sig ->
                val md = MessageDigest.getInstance("SHA-256")
                md.update(sig.toByteArray())
                android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP)
            }
        } catch (_: Exception) { null }
    }

    fun isDebuggable(context: Context): Boolean {
        return (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}
