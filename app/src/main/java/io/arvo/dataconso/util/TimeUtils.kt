package io.arvo.dataconso.util

import android.os.SystemClock

object TimeUtils {
    /**
     * Retourne le temps monotone en millisecondes.
     * Insensible aux changements d'heure manuels de l'utilisateur.
     */
    fun getMonotonicTime(): Long = SystemClock.elapsedRealtime()
    
    /**
     * Calcul de durée sécurisé contre la fraude temporelle.
     */
    fun getDurationSince(startTime: Long): Long = getMonotonicTime() - startTime
}
