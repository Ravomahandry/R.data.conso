package io.arvo.dataconso

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import java.security.KeyStore

/**
 * Sécurisation de niveau Sommité :
 * Gère la corruption du Keystore Android et assure une résilience totale.
 */
object SecurityUtils {
    private const val PREFS_NAME = "arvo_secure_prefs_v3"
    private const val KEY_ALIAS = MasterKey.DEFAULT_MASTER_KEY_ALIAS

    fun getEncryptedPrefs(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Throwable) {
            Log.e("ARVO_SECURITY", "Keystore error, falling back to clear prefs for stability", e)
            // En cas d'échec du Keystore (fréquent sur certains modèles), 
            // on bascule sur des SharedPreferences standards pour éviter le crash.
            context.getSharedPreferences("arvo_stable_prefs", Context.MODE_PRIVATE)
        }
    }

    fun getDatabasePassphrase(context: Context): ByteArray {
        val prefs = getEncryptedPrefs(context)
        var secret = prefs.getString("db_pass_secret_v4", null)
        if (secret == null) {
            // Sommité : Génération d'une clé 256-bit réelle encodée en Base64
            val key = ByteArray(32)
            java.security.SecureRandom().nextBytes(key)
            secret = android.util.Base64.encodeToString(key, android.util.Base64.NO_WRAP)
            prefs.edit().putString("db_pass_secret_v4", secret).apply()
        }
        // Rigueur SQLCipher 4.17+ : La passphrase doit être passée en ByteArray de la chaîne décodée 
        // ou la chaîne elle-même encodée correctement.
        val currentSecret = secret
        if (currentSecret != null) {
            return currentSecret.toByteArray()
        }
        return byteArrayOf()
    }
}
