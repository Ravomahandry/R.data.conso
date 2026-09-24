package io.arvo.dataconso.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J\u000e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\u0005\u00a8\u0006\b"}, d2 = {"Lio/arvo/dataconso/util/TimeUtils;", "", "<init>", "()V", "getMonotonicTime", "", "getDurationSince", "startTime", "app_debug"})
public final class TimeUtils {
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.util.TimeUtils INSTANCE = null;
    
    private TimeUtils() {
        super();
    }
    
    /**
     * Retourne le temps monotone en millisecondes.
     * Insensible aux changements d'heure manuels de l'utilisateur.
     */
    public final long getMonotonicTime() {
        return 0L;
    }
    
    /**
     * Calcul de durée sécurisé contre la fraude temporelle.
     */
    public final long getDurationSince(long startTime) {
        return 0L;
    }
}