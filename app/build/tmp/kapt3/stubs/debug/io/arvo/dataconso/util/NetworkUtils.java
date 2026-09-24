package io.arvo.dataconso.util;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\b\u00c6\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0004\u001a\u00020\u0005\u00a8\u0006\u0006"}, d2 = {"Lio/arvo/dataconso/util/NetworkUtils;", "", "<init>", "()V", "getWifiRxBytes", "", "app_debug"})
public final class NetworkUtils {
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.util.NetworkUtils INSTANCE = null;
    
    private NetworkUtils() {
        super();
    }
    
    /**
     * Sommité : Récupération précise des octets reçus sur l'interface WiFi uniquement.
     * Évite de compter le trafic Bluetooth, P2P ou Tethering.
     */
    public final long getWifiRxBytes() {
        return 0L;
    }
}