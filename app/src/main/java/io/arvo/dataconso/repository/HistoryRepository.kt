package io.arvo.dataconso.repository

import io.arvo.dataconso.HistoryEntry

/**
 * Interface pour l'accès aux données historiques.
 * Nécessaire pour découpler l'IA de la base de données Room.
 */
interface HistoryRepository {
    /**
     * Récupère l'historique des 30 derniers jours pour un utilisateur/SIM donné.
     */
    suspend fun getLast30Days(simId: String): List<HistoryEntry>
}
