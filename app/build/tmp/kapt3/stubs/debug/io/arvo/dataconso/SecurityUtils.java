package io.arvo.dataconso;

/**
 * Sécurisation de niveau Sommité :
 * Gère la corruption du Keystore Android et assure une résilience totale.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nJ\u000e\u0010\u000b\u001a\u00020\f2\u0006\u0010\t\u001a\u00020\nR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2 = {"Lio/arvo/dataconso/SecurityUtils;", "", "<init>", "()V", "PREFS_NAME", "", "KEY_ALIAS", "getEncryptedPrefs", "Landroid/content/SharedPreferences;", "context", "Landroid/content/Context;", "getDatabasePassphrase", "", "app_debug"})
public final class SecurityUtils {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String PREFS_NAME = "arvo_secure_prefs_v3";
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String KEY_ALIAS = "_androidx_security_master_key_";
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.SecurityUtils INSTANCE = null;
    
    private SecurityUtils() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final android.content.SharedPreferences getEncryptedPrefs(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final byte[] getDatabasePassphrase(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        return null;
    }
}