package io.arvo.dataconso;

/**
 * Enforcer d'Accessibilité ARVO (Audit Etape 5.2).
 * Gère le blocage visuel immédiat basé sur les décisions du processus Core.
 */
@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0016J\u0012\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0011H\u0002J\u0010\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u0011H\u0002J\u0018\u0010\u001d\u001a\u00020\u00162\u0006\u0010\u001e\u001a\u00020\u00112\u0006\u0010\u001f\u001a\u00020\u001aH\u0002J\b\u0010 \u001a\u00020\u0016H\u0016R \u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087.\u00a2\u0006\u0010\n\u0002\u0010\n\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR \u0010\u000b\u001a\u00020\u00058\u0006@\u0006X\u0087.\u00a2\u0006\u0010\n\u0002\u0010\n\u001a\u0004\b\f\u0010\u0007\"\u0004\b\r\u0010\tR\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0010\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0013X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0014\u001a\u0004\u0018\u00010\u0011X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2 = {"Lio/arvo/dataconso/ArvoAccessibilityService;", "Landroid/accessibilityservice/AccessibilityService;", "<init>", "()V", "database", "error/NonExistentClass", "getDatabase", "()Lerror/NonExistentClass;", "setDatabase", "(Lerror/NonExistentClass;)V", "Lerror/NonExistentClass;", "repository", "getRepository", "setRepository", "serviceScope", "Lkotlinx/coroutines/CoroutineScope;", "lastPackage", "", "lastBlockedTime", "", "lastBlockedPkg", "onAccessibilityEvent", "", "event", "Landroid/view/accessibility/AccessibilityEvent;", "isSafePackage", "", "pkg", "isLauncher", "checkSecurityAndBlock", "packageName", "isEventPackage", "onInterrupt", "app_debug"})
public final class ArvoAccessibilityService extends android.accessibilityservice.AccessibilityService {
    @javax.inject.Inject()
    public error.NonExistentClass database;
    @javax.inject.Inject()
    public error.NonExistentClass repository;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope serviceScope = null;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastPackage;
    private long lastBlockedTime = 0L;
    @org.jetbrains.annotations.Nullable()
    private java.lang.String lastBlockedPkg;
    
    public ArvoAccessibilityService() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final error.NonExistentClass getDatabase() {
        return null;
    }
    
    public final void setDatabase(@org.jetbrains.annotations.NotNull()
    error.NonExistentClass p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    public final error.NonExistentClass getRepository() {
        return null;
    }
    
    public final void setRepository(@org.jetbrains.annotations.NotNull()
    error.NonExistentClass p0) {
    }
    
    @java.lang.Override()
    public void onAccessibilityEvent(@org.jetbrains.annotations.NotNull()
    android.view.accessibility.AccessibilityEvent event) {
    }
    
    private final boolean isSafePackage(java.lang.String pkg) {
        return false;
    }
    
    private final boolean isLauncher(java.lang.String pkg) {
        return false;
    }
    
    private final void checkSecurityAndBlock(java.lang.String packageName, boolean isEventPackage) {
    }
    
    @java.lang.Override()
    public void onInterrupt() {
    }
}