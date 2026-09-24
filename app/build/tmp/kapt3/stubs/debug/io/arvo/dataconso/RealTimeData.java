package io.arvo.dataconso;

/**
 * Hub de Données Temps Réel ARVO (Elite 2.0).
 * Optimisé pour la distinction stricte Wi-Fi vs Mobile par application.
 */
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0018\n\u0002\u0010$\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001:\u0002CDB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u00ab\u0001\u0010.\u001a\u00020/2\u0006\u00100\u001a\u0002012\b\b\u0002\u00102\u001a\u00020\u000f2\b\b\u0002\u00103\u001a\u00020\u000f2\n\b\u0002\u00104\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\b2\b\b\u0002\u00105\u001a\u00020\u000f2\b\b\u0002\u00106\u001a\u00020\u000f2\b\b\u0002\u00107\u001a\u00020\u000f2\b\b\u0002\u00108\u001a\u00020\u000f2\b\b\u0002\u00109\u001a\u00020\u000f2\b\b\u0002\u0010:\u001a\u00020\u000f2\u0016\b\u0002\u0010)\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000f\u0018\u00010(2\u0016\b\u0002\u0010,\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000f\u0018\u00010(\u00a2\u0006\u0002\u0010;J\u0016\u0010<\u001a\u00020/2\u0006\u0010=\u001a\u00020\u000f2\u0006\u0010>\u001a\u00020\u000fJ\u0016\u0010?\u001a\u00020/2\u0006\u0010=\u001a\u00020\u000f2\u0006\u0010>\u001a\u00020\u000fJ\u0010\u0010@\u001a\u00020/2\u0006\u0010A\u001a\u00020BH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\t\u001a\b\u0012\u0004\u0012\u00020\b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u000bR\u0014\u0010\f\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0014\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u000f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000bR\u0014\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u000f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000bR\u0014\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u000f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u000bR\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u000bR\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u000bR\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u000f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\u000bR\u0014\u0010!\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\"\u001a\b\u0012\u0004\u0012\u00020\u000f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b#\u0010\u000bR\u0014\u0010$\u001a\b\u0012\u0004\u0012\u00020\u000f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010%\u001a\b\u0012\u0004\u0012\u00020\u000f0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u0010\u000bR \u0010\'\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000f0(0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010)\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000f0(0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b*\u0010\u000bR \u0010+\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000f0(0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R#\u0010,\u001a\u0014\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000f0(0\n\u00a2\u0006\b\n\u0000\u001a\u0004\b-\u0010\u000b\u00a8\u0006E"}, d2 = {"Lio/arvo/dataconso/RealTimeData;", "", "<init>", "()V", "ACTION_SYNC", "", "_isVpnRunning", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "isVpnRunning", "Lkotlinx/coroutines/flow/StateFlow;", "()Lkotlinx/coroutines/flow/StateFlow;", "_isTunnelActive", "isTunnelActive", "_dlSpeed", "", "dlSpeed", "getDlSpeed", "_ulSpeed", "ulSpeed", "getUlSpeed", "_todayWifiBytes", "todayWifiBytes", "getTodayWifiBytes", "_todayMobileBytes", "todayMobileBytes", "getTodayMobileBytes", "_weekWifiBytes", "weekWifiBytes", "getWeekWifiBytes", "_weekMobileBytes", "weekMobileBytes", "getWeekMobileBytes", "_monthWifiBytes", "monthWifiBytes", "getMonthWifiBytes", "_monthMobileBytes", "monthMobileBytes", "getMonthMobileBytes", "_appUsagesWifi", "", "appUsagesWifi", "getAppUsagesWifi", "_appUsagesMobile", "appUsagesMobile", "getAppUsagesMobile", "updateAndBroadcast", "", "context", "Landroid/content/Context;", "dl", "ul", "isRunning", "wifiToday", "mobileToday", "wifiWeek", "mobileWeek", "wifiMonth", "mobileMonth", "(Landroid/content/Context;JJLjava/lang/Boolean;Ljava/lang/Boolean;JJJJJJLjava/util/Map;Ljava/util/Map;)V", "updateMonthUsage", "wifi", "mobile", "updateWeekUsage", "applyData", "data", "Lio/arvo/dataconso/RealTimeData$SyncData;", "SyncData", "SyncReceiver", "app_debug"})
public final class RealTimeData {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String ACTION_SYNC = "io.arvo.dataconso.SYNC_REALTIME";
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isVpnRunning = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isVpnRunning = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Boolean> _isTunnelActive = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isTunnelActive = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _dlSpeed = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Long> dlSpeed = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _ulSpeed = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Long> ulSpeed = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _todayWifiBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Long> todayWifiBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _todayMobileBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Long> todayMobileBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _weekWifiBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Long> weekWifiBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _weekMobileBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Long> weekMobileBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _monthWifiBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Long> monthWifiBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.lang.Long> _monthMobileBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.lang.Long> monthMobileBytes = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.util.Map<java.lang.String, java.lang.Long>> _appUsagesWifi = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, java.lang.Long>> appUsagesWifi = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.MutableStateFlow<java.util.Map<java.lang.String, java.lang.Long>> _appUsagesMobile = null;
    @org.jetbrains.annotations.NotNull()
    private static final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, java.lang.Long>> appUsagesMobile = null;
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.RealTimeData INSTANCE = null;
    
    private RealTimeData() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isVpnRunning() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Boolean> isTunnelActive() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getDlSpeed() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getUlSpeed() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getTodayWifiBytes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getTodayMobileBytes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getWeekWifiBytes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getWeekMobileBytes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getMonthWifiBytes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.lang.Long> getMonthMobileBytes() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, java.lang.Long>> getAppUsagesWifi() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.Map<java.lang.String, java.lang.Long>> getAppUsagesMobile() {
        return null;
    }
    
    public final void updateAndBroadcast(@org.jetbrains.annotations.NotNull()
    android.content.Context context, long dl, long ul, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean isRunning, @org.jetbrains.annotations.Nullable()
    java.lang.Boolean isTunnelActive, long wifiToday, long mobileToday, long wifiWeek, long mobileWeek, long wifiMonth, long mobileMonth, @org.jetbrains.annotations.Nullable()
    java.util.Map<java.lang.String, java.lang.Long> appUsagesWifi, @org.jetbrains.annotations.Nullable()
    java.util.Map<java.lang.String, java.lang.Long> appUsagesMobile) {
    }
    
    public final void updateMonthUsage(long wifi, long mobile) {
    }
    
    public final void updateWeekUsage(long wifi, long mobile) {
    }
    
    private final void applyData(io.arvo.dataconso.RealTimeData.SyncData data) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\b \n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u009f\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\f\u001a\u00020\u0003\u0012\b\b\u0002\u0010\r\u001a\u00020\u0003\u0012\u0016\b\u0002\u0010\u000e\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000f\u0012\u0016\b\u0002\u0010\u0011\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000f\u00a2\u0006\u0004\b\u0012\u0010\u0013J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010$\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\u0010\u0010%\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0017J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003J\t\u0010)\u001a\u00020\u0003H\u00c6\u0003J\t\u0010*\u001a\u00020\u0003H\u00c6\u0003J\t\u0010+\u001a\u00020\u0003H\u00c6\u0003J\u0017\u0010,\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000fH\u00c6\u0003J\u0017\u0010-\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000fH\u00c6\u0003J\u00a6\u0001\u0010.\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00062\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u00032\b\b\u0002\u0010\u000b\u001a\u00020\u00032\b\b\u0002\u0010\f\u001a\u00020\u00032\b\b\u0002\u0010\r\u001a\u00020\u00032\u0016\b\u0002\u0010\u000e\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000f2\u0016\b\u0002\u0010\u0011\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000fH\u00c6\u0001\u00a2\u0006\u0002\u0010/J\u0006\u00100\u001a\u000201J\u0013\u00102\u001a\u00020\u00062\b\u00103\u001a\u0004\u0018\u000104H\u00d6\u0003J\t\u00105\u001a\u000201H\u00d6\u0001J\t\u00106\u001a\u00020\u0010H\u00d6\u0001J\u0016\u00107\u001a\u0002082\u0006\u00109\u001a\u00020:2\u0006\u0010;\u001a\u000201R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0015R\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0018\u001a\u0004\b\u0005\u0010\u0017R\u0015\u0010\u0007\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0018\u001a\u0004\b\u0007\u0010\u0017R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0015R\u0011\u0010\t\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0015R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0015R\u0011\u0010\u000b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0015R\u0011\u0010\f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0015R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0015R\u001f\u0010\u000e\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u001f\u0010\u0011\u001a\u0010\u0012\u0004\u0012\u00020\u0010\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u0010 \u00a8\u0006<"}, d2 = {"Lio/arvo/dataconso/RealTimeData$SyncData;", "Landroid/os/Parcelable;", "dl", "", "ul", "isRunning", "", "isTunnelActive", "wifiT", "mobileT", "wifiW", "mobileW", "wifiM", "mobileM", "appUsagesWifi", "", "", "appUsagesMobile", "<init>", "(JJLjava/lang/Boolean;Ljava/lang/Boolean;JJJJJJLjava/util/Map;Ljava/util/Map;)V", "getDl", "()J", "getUl", "()Ljava/lang/Boolean;", "Ljava/lang/Boolean;", "getWifiT", "getMobileT", "getWifiW", "getMobileW", "getWifiM", "getMobileM", "getAppUsagesWifi", "()Ljava/util/Map;", "getAppUsagesMobile", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "copy", "(JJLjava/lang/Boolean;Ljava/lang/Boolean;JJJJJJLjava/util/Map;Ljava/util/Map;)Lio/arvo/dataconso/RealTimeData$SyncData;", "describeContents", "", "equals", "other", "", "hashCode", "toString", "writeToParcel", "", "dest", "Landroid/os/Parcel;", "flags", "app_debug"})
    @kotlinx.parcelize.Parcelize()
    public static final class SyncData implements android.os.Parcelable {
        private final long dl = 0L;
        private final long ul = 0L;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Boolean isRunning = null;
        @org.jetbrains.annotations.Nullable()
        private final java.lang.Boolean isTunnelActive = null;
        private final long wifiT = 0L;
        private final long mobileT = 0L;
        private final long wifiW = 0L;
        private final long mobileW = 0L;
        private final long wifiM = 0L;
        private final long mobileM = 0L;
        @org.jetbrains.annotations.Nullable()
        private final java.util.Map<java.lang.String, java.lang.Long> appUsagesWifi = null;
        @org.jetbrains.annotations.Nullable()
        private final java.util.Map<java.lang.String, java.lang.Long> appUsagesMobile = null;
        
        @java.lang.Override()
        public final int describeContents() {
            return 0;
        }
        
        @java.lang.Override()
        public final void writeToParcel(@org.jetbrains.annotations.NotNull()
        android.os.Parcel dest, int flags) {
        }
        
        public SyncData(long dl, long ul, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean isRunning, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean isTunnelActive, long wifiT, long mobileT, long wifiW, long mobileW, long wifiM, long mobileM, @org.jetbrains.annotations.Nullable()
        java.util.Map<java.lang.String, java.lang.Long> appUsagesWifi, @org.jetbrains.annotations.Nullable()
        java.util.Map<java.lang.String, java.lang.Long> appUsagesMobile) {
            super();
        }
        
        public final long getDl() {
            return 0L;
        }
        
        public final long getUl() {
            return 0L;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean isRunning() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean isTunnelActive() {
            return null;
        }
        
        public final long getWifiT() {
            return 0L;
        }
        
        public final long getMobileT() {
            return 0L;
        }
        
        public final long getWifiW() {
            return 0L;
        }
        
        public final long getMobileW() {
            return 0L;
        }
        
        public final long getWifiM() {
            return 0L;
        }
        
        public final long getMobileM() {
            return 0L;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.util.Map<java.lang.String, java.lang.Long> getAppUsagesWifi() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.util.Map<java.lang.String, java.lang.Long> getAppUsagesMobile() {
            return null;
        }
        
        public SyncData() {
            super();
        }
        
        public final long component1() {
            return 0L;
        }
        
        public final long component10() {
            return 0L;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.util.Map<java.lang.String, java.lang.Long> component11() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.util.Map<java.lang.String, java.lang.Long> component12() {
            return null;
        }
        
        public final long component2() {
            return 0L;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean component3() {
            return null;
        }
        
        @org.jetbrains.annotations.Nullable()
        public final java.lang.Boolean component4() {
            return null;
        }
        
        public final long component5() {
            return 0L;
        }
        
        public final long component6() {
            return 0L;
        }
        
        public final long component7() {
            return 0L;
        }
        
        public final long component8() {
            return 0L;
        }
        
        public final long component9() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.RealTimeData.SyncData copy(long dl, long ul, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean isRunning, @org.jetbrains.annotations.Nullable()
        java.lang.Boolean isTunnelActive, long wifiT, long mobileT, long wifiW, long mobileW, long wifiM, long mobileM, @org.jetbrains.annotations.Nullable()
        java.util.Map<java.lang.String, java.lang.Long> appUsagesWifi, @org.jetbrains.annotations.Nullable()
        java.util.Map<java.lang.String, java.lang.Long> appUsagesMobile) {
            return null;
        }
        
        @java.lang.Override()
        public boolean equals(@org.jetbrains.annotations.Nullable()
        java.lang.Object other) {
            return false;
        }
        
        @java.lang.Override()
        public int hashCode() {
            return 0;
        }
        
        @java.lang.Override()
        @org.jetbrains.annotations.NotNull()
        public java.lang.String toString() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0016\u00a8\u0006\n"}, d2 = {"Lio/arvo/dataconso/RealTimeData$SyncReceiver;", "Landroid/content/BroadcastReceiver;", "<init>", "()V", "onReceive", "", "context", "Landroid/content/Context;", "intent", "Landroid/content/Intent;", "app_debug"})
    public static final class SyncReceiver extends android.content.BroadcastReceiver {
        
        public SyncReceiver() {
            super();
        }
        
        @java.lang.Override()
        public void onReceive(@org.jetbrains.annotations.Nullable()
        android.content.Context context, @org.jetbrains.annotations.Nullable()
        android.content.Intent intent) {
        }
    }
}