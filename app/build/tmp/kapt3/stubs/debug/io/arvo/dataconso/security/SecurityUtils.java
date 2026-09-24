package io.arvo.dataconso.security;

/**
 * Sommité Security Suite (Phase 5)
 * Root detection and Integrity checks.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005J\u000e\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bJ\u000e\u0010\t\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\b\u00a8\u0006\n"}, d2 = {"Lio/arvo/dataconso/security/SecurityUtils;", "", "<init>", "()V", "isDeviceRooted", "", "checkIntegrity", "context", "Landroid/content/Context;", "isLowEndDevice", "app_debug"})
public final class SecurityUtils {
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.security.SecurityUtils INSTANCE = null;
    
    private SecurityUtils() {
        super();
    }
    
    public final boolean isDeviceRooted() {
        return false;
    }
    
    public final boolean checkIntegrity(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
    
    public final boolean isLowEndDevice(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return false;
    }
}