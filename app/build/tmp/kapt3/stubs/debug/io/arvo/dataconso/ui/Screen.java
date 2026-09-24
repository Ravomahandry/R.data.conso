package io.arvo.dataconso.ui;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0010\u0011\u0012\u0013\u0014B!\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u0082\u0001\u0005\u0015\u0016\u0017\u0018\u0019\u00a8\u0006\u001a"}, d2 = {"Lio/arvo/dataconso/ui/Screen;", "", "route", "", "labelRes", "", "icon", "Landroidx/compose/ui/graphics/vector/ImageVector;", "<init>", "(Ljava/lang/String;ILandroidx/compose/ui/graphics/vector/ImageVector;)V", "getRoute", "()Ljava/lang/String;", "getLabelRes", "()I", "getIcon", "()Landroidx/compose/ui/graphics/vector/ImageVector;", "Dashboard", "Analysis", "Quotas", "Settings", "GhostMode", "Lio/arvo/dataconso/ui/Screen$Analysis;", "Lio/arvo/dataconso/ui/Screen$Dashboard;", "Lio/arvo/dataconso/ui/Screen$GhostMode;", "Lio/arvo/dataconso/ui/Screen$Quotas;", "Lio/arvo/dataconso/ui/Screen$Settings;", "app_debug"})
public abstract class Screen {
    @org.jetbrains.annotations.NotNull()
    private final java.lang.String route = null;
    private final int labelRes = 0;
    @org.jetbrains.annotations.NotNull()
    private final androidx.compose.ui.graphics.vector.ImageVector icon = null;
    
    private Screen(java.lang.String route, int labelRes, androidx.compose.ui.graphics.vector.ImageVector icon) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getRoute() {
        return null;
    }
    
    public final int getLabelRes() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final androidx.compose.ui.graphics.vector.ImageVector getIcon() {
        return null;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lio/arvo/dataconso/ui/Screen$Analysis;", "Lio/arvo/dataconso/ui/Screen;", "<init>", "()V", "app_debug"})
    public static final class Analysis extends io.arvo.dataconso.ui.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final io.arvo.dataconso.ui.Screen.Analysis INSTANCE = null;
        
        private Analysis() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lio/arvo/dataconso/ui/Screen$Dashboard;", "Lio/arvo/dataconso/ui/Screen;", "<init>", "()V", "app_debug"})
    public static final class Dashboard extends io.arvo.dataconso.ui.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final io.arvo.dataconso.ui.Screen.Dashboard INSTANCE = null;
        
        private Dashboard() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lio/arvo/dataconso/ui/Screen$GhostMode;", "Lio/arvo/dataconso/ui/Screen;", "<init>", "()V", "app_debug"})
    public static final class GhostMode extends io.arvo.dataconso.ui.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final io.arvo.dataconso.ui.Screen.GhostMode INSTANCE = null;
        
        private GhostMode() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lio/arvo/dataconso/ui/Screen$Quotas;", "Lio/arvo/dataconso/ui/Screen;", "<init>", "()V", "app_debug"})
    public static final class Quotas extends io.arvo.dataconso.ui.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final io.arvo.dataconso.ui.Screen.Quotas INSTANCE = null;
        
        private Quotas() {
        }
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lio/arvo/dataconso/ui/Screen$Settings;", "Lio/arvo/dataconso/ui/Screen;", "<init>", "()V", "app_debug"})
    public static final class Settings extends io.arvo.dataconso.ui.Screen {
        @org.jetbrains.annotations.NotNull()
        public static final io.arvo.dataconso.ui.Screen.Settings INSTANCE = null;
        
        private Settings() {
        }
    }
}