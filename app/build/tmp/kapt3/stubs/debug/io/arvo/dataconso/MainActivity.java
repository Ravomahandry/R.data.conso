package io.arvo.dataconso;

@dagger.hilt.android.AndroidEntryPoint()
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u0014R\u001e\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\t\u00a8\u0006\u000f"}, d2 = {"Lio/arvo/dataconso/MainActivity;", "Landroidx/activity/ComponentActivity;", "<init>", "()V", "cloudSyncManager", "Lio/arvo/dataconso/backend/CloudSyncManager;", "getCloudSyncManager", "()Lio/arvo/dataconso/backend/CloudSyncManager;", "setCloudSyncManager", "(Lio/arvo/dataconso/backend/CloudSyncManager;)V", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "Companion", "app_debug"})
public final class MainActivity extends androidx.activity.ComponentActivity {
    @javax.inject.Inject()
    public io.arvo.dataconso.backend.CloudSyncManager cloudSyncManager;
    @org.jetbrains.annotations.NotNull()
    private static final java.util.List<kotlin.Triple<java.lang.String, java.lang.String, java.lang.Integer>> APP_LANGUAGES = null;
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.MainActivity.Companion Companion = null;
    
    public MainActivity() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final io.arvo.dataconso.backend.CloudSyncManager getCloudSyncManager() {
        return null;
    }
    
    public final void setCloudSyncManager(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.backend.CloudSyncManager p0) {
    }
    
    @java.lang.Override()
    protected void onCreate(@org.jetbrains.annotations.Nullable()
    android.os.Bundle savedInstanceState) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\b\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R)\u0010\u0004\u001a\u001a\u0012\u0016\u0012\u0014\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\u0007\u0012\u0004\u0012\u00020\b0\u00060\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\n\u00a8\u0006\u000b"}, d2 = {"Lio/arvo/dataconso/MainActivity$Companion;", "", "<init>", "()V", "APP_LANGUAGES", "", "Lkotlin/Triple;", "", "", "getAPP_LANGUAGES", "()Ljava/util/List;", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.util.List<kotlin.Triple<java.lang.String, java.lang.String, java.lang.Integer>> getAPP_LANGUAGES() {
            return null;
        }
    }
}