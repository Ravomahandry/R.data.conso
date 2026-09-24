package io.arvo.dataconso.domain.usecase;

/**
 * UseCase spécialisé pour le calcul réactif de l'état des quotas applicatifs.
 * Sommité : Utilise une fusion directe des flux pour garantir la réactivité temps réel.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0010\t\n\u0002\b\u0002\u0018\u00002\u00020\u0001B#\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ=\u0010\u000b\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\r0\f2\u0012\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000f2\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u00110\u000fH\u0086\u0002R\u0010\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0013"}, d2 = {"Lio/arvo/dataconso/domain/usecase/GetAppQuotasUseCase;", "", "repository", "error/NonExistentClass", "usageManager", "Lio/arvo/dataconso/DataUsageManager;", "context", "Landroid/content/Context;", "<init>", "(Lerror/NonExistentClass;Lio/arvo/dataconso/DataUsageManager;Landroid/content/Context;)V", "Lerror/NonExistentClass;", "invoke", "Lkotlinx/coroutines/flow/Flow;", "", "rtAppWifi", "", "", "", "rtAppMobile", "app_debug"})
public final class GetAppQuotasUseCase {
    @org.jetbrains.annotations.NotNull()
    private final error.NonExistentClass repository = null;
    @org.jetbrains.annotations.NotNull()
    private final io.arvo.dataconso.DataUsageManager usageManager = null;
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    
    @javax.inject.Inject()
    public GetAppQuotasUseCase(@org.jetbrains.annotations.NotNull()
    error.NonExistentClass repository, @org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.DataUsageManager usageManager, @dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<java.util.List<error.NonExistentClass>> invoke(@org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Long> rtAppWifi, @org.jetbrains.annotations.NotNull()
    java.util.Map<java.lang.String, java.lang.Long> rtAppMobile) {
        return null;
    }
}