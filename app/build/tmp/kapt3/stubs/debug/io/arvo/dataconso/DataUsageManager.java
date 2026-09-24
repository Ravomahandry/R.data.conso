package io.arvo.dataconso;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010%\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010$\n\u0002\b\u0005\u0018\u0000 32\u00020\u0001:\u0003345B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u0006\u001a\u00020\u0007J(\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000eJ2\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u000e2\b\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u000b2\u0006\u0010\u0017\u001a\u00020\u000bH\u0002J\u0012\u0010\u0018\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u000f\u001a\u00020\u000eH\u0003J&\u0010\u0019\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00152\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eJ\u001e\u0010\u001b\u001a\u00020\t2\u0006\u0010\u001a\u001a\u00020\u00152\u0006\u0010\u001c\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010\u001d\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\u0015J \u0010\u001e\u001a\u00020\t2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000eJ \u0010!\u001a\u00020\t2\u0006\u0010\"\u001a\u00020\u000e2\u0006\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000eJ\u0018\u0010#\u001a\u00020\u000b2\u0006\u0010\"\u001a\u00020\u000e2\b\b\u0002\u0010$\u001a\u00020%J\u000e\u0010&\u001a\u00020\u00152\u0006\u0010\'\u001a\u00020\u000bJ\u000e\u0010(\u001a\u00020\u00152\u0006\u0010\'\u001a\u00020\u000bJ\u000e\u0010)\u001a\u00020\u00152\u0006\u0010*\u001a\u00020\u000bJ.\u0010-\u001a\b\u0012\u0004\u0012\u00020/0.2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u000eJ8\u00100\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\t012\f\u00102\u001a\b\u0012\u0004\u0012\u00020\u00150.2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\u0006\u0010\r\u001a\u00020\u000eR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010+\u001a\u000e\u0012\u0004\u0012\u00020\u0015\u0012\u0004\u0012\u00020\u00150,X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00066"}, d2 = {"Lio/arvo/dataconso/DataUsageManager;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "hasUsageStatsPermission", "", "getUsageBreakdownForRange", "Lio/arvo/dataconso/DataUsageManager$UsageBreakdown;", "startTime", "", "endTime", "networkType", "", "subId", "getUsageForType", "nsm", "Landroid/app/usage/NetworkStatsManager;", "type", "subscriberId", "", "start", "end", "getSubscriberIdForSub", "getAppUsageForRange", "packageName", "getAppUsageForDay", "dayMillis", "getUidRealTimeUsage", "getUsageBreakdownForPeriod", "period", "Lio/arvo/dataconso/DataUsageManager$PeriodType;", "getUsageBreakdownForBillingCycle", "billingCycleDay", "getBillingCycleStartMillis", "now", "Ljava/util/Calendar;", "formatData", "bytes", "formatDataCompact", "formatSpeed", "bytesPerSec", "labelCache", "", "getTopAppsUsage", "", "Lio/arvo/dataconso/AppUsageInfo;", "getMultiAppUsage", "", "packageNames", "Companion", "PeriodType", "UsageBreakdown", "app_debug"})
public final class DataUsageManager {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final java.util.Map<java.lang.String, java.lang.String> labelCache = null;
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.DataUsageManager.Companion Companion = null;
    
    public DataUsageManager(@org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    public final boolean hasUsageStatsPermission() {
        return false;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.arvo.dataconso.DataUsageManager.UsageBreakdown getUsageBreakdownForRange(long startTime, long endTime, int networkType, int subId) {
        return null;
    }
    
    private final io.arvo.dataconso.DataUsageManager.UsageBreakdown getUsageForType(android.app.usage.NetworkStatsManager nsm, int type, java.lang.String subscriberId, long start, long end) {
        return null;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    private final java.lang.String getSubscriberIdForSub(int subId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.arvo.dataconso.DataUsageManager.UsageBreakdown getAppUsageForRange(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, long startTime, long endTime, int networkType) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.arvo.dataconso.DataUsageManager.UsageBreakdown getAppUsageForDay(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, long dayMillis, int networkType) {
        return null;
    }
    
    public final long getUidRealTimeUsage(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName) {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.arvo.dataconso.DataUsageManager.UsageBreakdown getUsageBreakdownForPeriod(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.DataUsageManager.PeriodType period, int networkType, int subId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.arvo.dataconso.DataUsageManager.UsageBreakdown getUsageBreakdownForBillingCycle(int billingCycleDay, int networkType, int subId) {
        return null;
    }
    
    public final long getBillingCycleStartMillis(int billingCycleDay, @org.jetbrains.annotations.NotNull()
    java.util.Calendar now) {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatData(long bytes) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatDataCompact(long bytes) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String formatSpeed(long bytesPerSec) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.List<io.arvo.dataconso.AppUsageInfo> getTopAppsUsage(long startTime, long endTime, int networkType, int subId) {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.util.Map<java.lang.String, io.arvo.dataconso.DataUsageManager.UsageBreakdown> getMultiAppUsage(@org.jetbrains.annotations.NotNull()
    java.util.List<java.lang.String> packageNames, long startTime, long endTime, int networkType) {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007\u00a8\u0006\b"}, d2 = {"Lio/arvo/dataconso/DataUsageManager$Companion;", "", "<init>", "()V", "humanReadable", "", "bytes", "", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String humanReadable(long bytes) {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2 = {"Lio/arvo/dataconso/DataUsageManager$PeriodType;", "", "<init>", "(Ljava/lang/String;I)V", "DAILY", "WEEKLY", "MONTHLY", "app_debug"})
    public static enum PeriodType {
        /*public static final*/ DAILY /* = new DAILY() */,
        /*public static final*/ WEEKLY /* = new WEEKLY() */,
        /*public static final*/ MONTHLY /* = new MONTHLY() */;
        
        PeriodType() {
        }
        
        @org.jetbrains.annotations.NotNull()
        public static kotlin.enums.EnumEntries<io.arvo.dataconso.DataUsageManager.PeriodType> getEntries() {
            return null;
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\n\u001a\u00020\u00038F\u00a2\u0006\u0006\u001a\u0004\b\u000b\u0010\b\u00a8\u0006\u0016"}, d2 = {"Lio/arvo/dataconso/DataUsageManager$UsageBreakdown;", "", "downloadBytes", "", "uploadBytes", "<init>", "(JJ)V", "getDownloadBytes", "()J", "getUploadBytes", "totalBytes", "getTotalBytes", "component1", "component2", "copy", "equals", "", "other", "hashCode", "", "toString", "", "app_debug"})
    public static final class UsageBreakdown {
        private final long downloadBytes = 0L;
        private final long uploadBytes = 0L;
        
        public UsageBreakdown(long downloadBytes, long uploadBytes) {
            super();
        }
        
        public final long getDownloadBytes() {
            return 0L;
        }
        
        public final long getUploadBytes() {
            return 0L;
        }
        
        public final long getTotalBytes() {
            return 0L;
        }
        
        public final long component1() {
            return 0L;
        }
        
        public final long component2() {
            return 0L;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.DataUsageManager.UsageBreakdown copy(long downloadBytes, long uploadBytes) {
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
}