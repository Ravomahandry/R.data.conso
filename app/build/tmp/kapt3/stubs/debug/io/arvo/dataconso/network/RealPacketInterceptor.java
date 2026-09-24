package io.arvo.dataconso.network;

/**
 * Intercepteur de paquets ARVO - Phase Expert (NDK v2).
 * Utilise des flags atomiques natifs pour une latence proche de zéro.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\u0002\b\u0007\u0018\u0000 \u00162\u00020\u0001:\u0001\u0016B\t\b\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tJ\u000e\u0010\r\u001a\u00020\u000b2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0006\u0010\u0010\u001a\u00020\u000bJ\u0011\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\tH\u0082 J\t\u0010\u0012\u001a\u00020\u000bH\u0082 J\u0019\u0010\u0013\u001a\u00020\u000b2\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\f\u001a\u00020\tH\u0082 R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0017"}, d2 = {"Lio/arvo/dataconso/network/RealPacketInterceptor;", "", "<init>", "()V", "interceptionJob", "Lkotlinx/coroutines/Job;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "currentGlobal", "", "updateBlockingState", "", "global", "startInterception", "pfd", "Landroid/os/ParcelFileDescriptor;", "stopInterception", "updateNativeState", "stopNativeLoop", "runNativePacketLoop", "fd", "", "Companion", "app_debug"})
public final class RealPacketInterceptor {
    @org.jetbrains.annotations.Nullable()
    private kotlinx.coroutines.Job interceptionJob;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.CoroutineScope scope = null;
    private boolean currentGlobal = false;
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.network.RealPacketInterceptor.Companion Companion = null;
    
    @javax.inject.Inject()
    public RealPacketInterceptor() {
        super();
    }
    
    public final void updateBlockingState(boolean global) {
    }
    
    public final void startInterception(@org.jetbrains.annotations.NotNull()
    android.os.ParcelFileDescriptor pfd) {
    }
    
    public final void stopInterception() {
    }
    
    private final native void updateNativeState(boolean global) {
    }
    
    private final native void stopNativeLoop() {
    }
    
    private final native void runNativePacketLoop(int fd, boolean global) {
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2 = {"Lio/arvo/dataconso/network/RealPacketInterceptor$Companion;", "", "<init>", "()V", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}