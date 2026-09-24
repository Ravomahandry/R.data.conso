package io.arvo.dataconso.network

import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Résolveur DNS ARVO avec filtrage de trackers intégré.
 * Optimisé pour supporter des milliers de domaines.
 */
@Singleton
class DnsResolver @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val fixedDomains = HashSet<String>()
    private val wildcardFilters = mutableListOf<String>()
    private val dnsCache = ConcurrentHashMap<String, String>()
    private var isLoaded = false

    /**
     * Charge la liste noire depuis les assets.
     */
    suspend fun loadBlockList() = withContext(Dispatchers.IO) {
        if (isLoaded) return@withContext
        try {
            context.assets.open("adblock_filters.txt").bufferedReader().useLines { lines ->
                lines.forEach { line ->
                    val entry = line.trim().lowercase()
                    if (entry.isNotEmpty() && !entry.startsWith("#")) {
                        if (entry.startsWith("*.")) {
                            wildcardFilters.add(entry.substring(2)) // Store suffix (e.g. "ads.com")
                        } else {
                            fixedDomains.add(entry)
                        }
                    }
                }
            }
            isLoaded = true
            Log.d("DnsResolver", "Loaded ${fixedDomains.size} domains and ${wildcardFilters.size} wildcards")
        } catch (e: Exception) {
            Log.e("DnsResolver", "Failed to load blocklist", e)
            // Fallback basics
            fixedDomains.add("doubleclick.net")
            fixedDomains.add("google-analytics.com")
        }
    }

    /**
     * Vérifie si un domaine doit être bloqué.
     * Optimisé : O(1) pour domaines fixes, O(W) pour wildcards.
     */
    fun shouldBlockDomain(domain: String): Boolean {
        val cleanDomain = domain.lowercase()
        
        // 1. Check exact matches (Fast O(1))
        if (fixedDomains.contains(cleanDomain)) return true
        
        // 2. Check wildcard suffixes (O(W) where W is number of wildcards)
        if (wildcardFilters.isEmpty()) return false
        
        return wildcardFilters.any { suffix ->
            cleanDomain.endsWith(suffix)
        }
    }

    /**
     * Simule une résolution DNS avec filtrage.
     * Retourne null pour simuler NXDOMAIN (bloqué).
     */
    suspend fun resolveDns(query: String): String? = withContext(Dispatchers.IO) {
        if (shouldBlockDomain(query)) return@withContext null
        
        // Retourne une IP fictive pour le test ou délègue au système
        dnsCache[query] ?: "8.8.8.8" 
    }

    fun clearCache() {
        dnsCache.clear()
    }
}
