package io.arvo.dataconso.data;

@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001:\u0001\u0016B\u0013\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\bH\u0086@\u00a2\u0006\u0002\u0010\u000eJ\u0010\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u000e\u0010\u0012\u001a\u00020\u0013H\u0086@\u00a2\u0006\u0002\u0010\u0014J\u000e\u0010\u0015\u001a\u00020\fH\u0086@\u00a2\u0006\u0002\u0010\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u0017"}, d2 = {"Lio/arvo/dataconso/data/SettingsDataStore;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "settingsFlow", "Lkotlinx/coroutines/flow/Flow;", "Lio/arvo/dataconso/data/AppSettings;", "getSettingsFlow", "()Lkotlinx/coroutines/flow/Flow;", "updateSettings", "", "newSettings", "(Lio/arvo/dataconso/data/AppSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "mapUserPreferences", "preferences", "Landroidx/datastore/preferences/core/Preferences;", "isMigrationCompleted", "", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "setMigrationCompleted", "PreferencesKeys", "app_debug"})
public final class SettingsDataStore {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.Flow<io.arvo.dataconso.data.AppSettings> settingsFlow = null;
    
    @javax.inject.Inject()
    public SettingsDataStore(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.Flow<io.arvo.dataconso.data.AppSettings> getSettingsFlow() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object updateSettings(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.data.AppSettings newSettings, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    private final io.arvo.dataconso.data.AppSettings mapUserPreferences(androidx.datastore.preferences.core.Preferences preferences) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object isMigrationCompleted(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.lang.Boolean> $completion) {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Object setMigrationCompleted(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u0006\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u000e\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\t\n\u0002\b\u0010\b\u00c2\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\bR\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\bR\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\bR\u0017\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\bR\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\bR\u0017\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\bR\u0017\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\bR\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\bR\u0017\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\bR\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\bR\u0017\u0010 \u001a\b\u0012\u0004\u0012\u00020\u001e0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010\bR\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\bR\u0017\u0010$\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u0010\bR\u0017\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\'\u0010\bR\u0017\u0010(\u001a\b\u0012\u0004\u0012\u00020\f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b)\u0010\bR\u0017\u0010*\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b+\u0010\bR\u0017\u0010,\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\bR\u0017\u0010.\u001a\b\u0012\u0004\u0012\u00020/0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b0\u0010\bR\u0017\u00101\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u0010\bR\u0017\u00103\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u0010\bR\u0017\u00105\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b6\u0010\bR\u0017\u00107\u001a\b\u0012\u0004\u0012\u00020/0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u0010\bR\u0017\u00109\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010\bR\u0017\u0010;\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u0010\bR\u0017\u0010=\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u0010\b\u00a8\u0006?"}, d2 = {"Lio/arvo/dataconso/data/SettingsDataStore$PreferencesKeys;", "", "<init>", "()V", "MONTHLY_WIFI_GB", "Landroidx/datastore/preferences/core/Preferences$Key;", "", "getMONTHLY_WIFI_GB", "()Landroidx/datastore/preferences/core/Preferences$Key;", "MONTHLY_MOBILE_GB", "getMONTHLY_MOBILE_GB", "BILLING_CYCLE_DAY", "", "getBILLING_CYCLE_DAY", "IS_HISTORY_INITIALIZED", "", "getIS_HISTORY_INITIALIZED", "VPN_ENABLED", "getVPN_ENABLED", "APP_FIREWALL_ENABLED", "getAPP_FIREWALL_ENABLED", "SPEED_ENABLED", "getSPEED_ENABLED", "GHOST_MODE_ENABLED", "getGHOST_MODE_ENABLED", "DAILY_LIMIT_GB", "getDAILY_LIMIT_GB", "NOTIFICATIONS_ENABLED", "getNOTIFICATIONS_ENABLED", "SELECTED_THEME", "", "getSELECTED_THEME", "SELECTED_LANGUAGE", "getSELECTED_LANGUAGE", "ONBOARDING_COMPLETED", "getONBOARDING_COMPLETED", "LAST_RESET_DATE", "getLAST_RESET_DATE", "DATA_SAVED_MB", "getDATA_SAVED_MB", "BLOCKED_TRACKERS_COUNT", "getBLOCKED_TRACKERS_COUNT", "DNS_PROVIDER", "getDNS_PROVIDER", "IS_PREMIUM", "getIS_PREMIUM", "PREMIUM_EXPIRY_TIMESTAMP", "", "getPREMIUM_EXPIRY_TIMESTAMP", "HAS_REMOVED_ADS", "getHAS_REMOVED_ADS", "MIGRATION_COMPLETED", "getMIGRATION_COMPLETED", "LOW_PERFORMANCE_MODE", "getLOW_PERFORMANCE_MODE", "TEMPORARY_PREMIUM_EXPIRY", "getTEMPORARY_PREMIUM_EXPIRY", "TOTAL_MONEY_SAVED", "getTOTAL_MONEY_SAVED", "USER_EMAIL", "getUSER_EMAIL", "USER_DISPLAY_NAME", "getUSER_DISPLAY_NAME", "app_debug"})
    static final class PreferencesKeys {
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> MONTHLY_WIFI_GB = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> MONTHLY_MOBILE_GB = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> BILLING_CYCLE_DAY = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> IS_HISTORY_INITIALIZED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> VPN_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> APP_FIREWALL_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> SPEED_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> GHOST_MODE_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> DAILY_LIMIT_GB = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> NOTIFICATIONS_ENABLED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SELECTED_THEME = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> SELECTED_LANGUAGE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> ONBOARDING_COMPLETED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> LAST_RESET_DATE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> DATA_SAVED_MB = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> BLOCKED_TRACKERS_COUNT = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> DNS_PROVIDER = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> IS_PREMIUM = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> PREMIUM_EXPIRY_TIMESTAMP = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> HAS_REMOVED_ADS = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> MIGRATION_COMPLETED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> LOW_PERFORMANCE_MODE = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> TEMPORARY_PREMIUM_EXPIRY = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> TOTAL_MONEY_SAVED = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> USER_EMAIL = null;
        @org.jetbrains.annotations.NotNull()
        private static final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> USER_DISPLAY_NAME = null;
        @org.jetbrains.annotations.NotNull()
        public static final io.arvo.dataconso.data.SettingsDataStore.PreferencesKeys INSTANCE = null;
        
        private PreferencesKeys() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> getMONTHLY_WIFI_GB() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> getMONTHLY_MOBILE_GB() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getBILLING_CYCLE_DAY() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getIS_HISTORY_INITIALIZED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getVPN_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getAPP_FIREWALL_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getSPEED_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getGHOST_MODE_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> getDAILY_LIMIT_GB() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getNOTIFICATIONS_ENABLED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getSELECTED_THEME() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getSELECTED_LANGUAGE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getONBOARDING_COMPLETED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getLAST_RESET_DATE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> getDATA_SAVED_MB() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Integer> getBLOCKED_TRACKERS_COUNT() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getDNS_PROVIDER() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getIS_PREMIUM() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> getPREMIUM_EXPIRY_TIMESTAMP() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getHAS_REMOVED_ADS() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getMIGRATION_COMPLETED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Boolean> getLOW_PERFORMANCE_MODE() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Long> getTEMPORARY_PREMIUM_EXPIRY() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.Double> getTOTAL_MONEY_SAVED() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getUSER_EMAIL() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final androidx.datastore.preferences.core.Preferences.Key<java.lang.String> getUSER_DISPLAY_NAME() {
            return null;
        }
    }
}