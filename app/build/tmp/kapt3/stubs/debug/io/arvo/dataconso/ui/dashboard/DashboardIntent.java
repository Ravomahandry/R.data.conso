package io.arvo.dataconso.ui.dashboard;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0003\u0004\u0005\u0006B\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0003\u0007\b\t\u00a8\u0006\n"}, d2 = {"Lio/arvo/dataconso/ui/dashboard/DashboardIntent;", "", "<init>", "()V", "Refresh", "SelectSource", "SelectGranularity", "Lio/arvo/dataconso/ui/dashboard/DashboardIntent$Refresh;", "Lio/arvo/dataconso/ui/dashboard/DashboardIntent$SelectGranularity;", "Lio/arvo/dataconso/ui/dashboard/DashboardIntent$SelectSource;", "app_debug"})
public abstract class DashboardIntent {
    
    private DashboardIntent() {
        super();
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lio/arvo/dataconso/ui/dashboard/DashboardIntent$Refresh;", "Lio/arvo/dataconso/ui/dashboard/DashboardIntent;", "<init>", "()V", "app_debug"})
    public static final class Refresh extends io.arvo.dataconso.ui.dashboard.DashboardIntent {
        @org.jetbrains.annotations.NotNull()
        public static final io.arvo.dataconso.ui.dashboard.DashboardIntent.Refresh INSTANCE = null;
        
        private Refresh() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lio/arvo/dataconso/ui/dashboard/DashboardIntent$SelectGranularity;", "Lio/arvo/dataconso/ui/dashboard/DashboardIntent;", "granularity", "Lio/arvo/dataconso/Granularity;", "<init>", "(Lio/arvo/dataconso/Granularity;)V", "getGranularity", "()Lio/arvo/dataconso/Granularity;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class SelectGranularity extends io.arvo.dataconso.ui.dashboard.DashboardIntent {
        @org.jetbrains.annotations.NotNull()
        private final io.arvo.dataconso.Granularity granularity = null;
        
        public SelectGranularity(@org.jetbrains.annotations.NotNull()
        io.arvo.dataconso.Granularity granularity) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.Granularity getGranularity() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.Granularity component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.ui.dashboard.DashboardIntent.SelectGranularity copy(@org.jetbrains.annotations.NotNull()
        io.arvo.dataconso.Granularity granularity) {
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
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0011H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0012"}, d2 = {"Lio/arvo/dataconso/ui/dashboard/DashboardIntent$SelectSource;", "Lio/arvo/dataconso/ui/dashboard/DashboardIntent;", "source", "Lio/arvo/dataconso/NetworkSource;", "<init>", "(Lio/arvo/dataconso/NetworkSource;)V", "getSource", "()Lio/arvo/dataconso/NetworkSource;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "", "app_debug"})
    public static final class SelectSource extends io.arvo.dataconso.ui.dashboard.DashboardIntent {
        @org.jetbrains.annotations.NotNull()
        private final io.arvo.dataconso.NetworkSource source = null;
        
        public SelectSource(@org.jetbrains.annotations.NotNull()
        io.arvo.dataconso.NetworkSource source) {
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.NetworkSource getSource() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.NetworkSource component1() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.ui.dashboard.DashboardIntent.SelectSource copy(@org.jetbrains.annotations.NotNull()
        io.arvo.dataconso.NetworkSource source) {
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