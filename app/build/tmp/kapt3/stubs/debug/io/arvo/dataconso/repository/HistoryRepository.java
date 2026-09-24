package io.arvo.dataconso.repository;

/**
 * Interface pour l'accès aux données historiques.
 * Nécessaire pour découpler l'IA de la base de données Room.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u00a6@\u00a2\u0006\u0002\u0010\u0007\u00a8\u0006\b\u00c0\u0006\u0003"}, d2 = {"Lio/arvo/dataconso/repository/HistoryRepository;", "", "getLast30Days", "", "error/NonExistentClass", "simId", "", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
public abstract interface HistoryRepository {
    
    /**
     * Récupère l'historique des 30 derniers jours pour un utilisateur/SIM donné.
     */
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getLast30Days(@org.jetbrains.annotations.NotNull()
    java.lang.String simId, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<error.NonExistentClass>> $completion);
}