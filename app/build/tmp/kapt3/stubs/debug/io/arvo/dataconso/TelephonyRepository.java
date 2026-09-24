package io.arvo.dataconso;

/**
 * Moteur de Téléphonie ARVO (Phase 4.1)
 * Gère l'identification des cartes SIM et la détection du flux de données actif.
 */
@javax.inject.Singleton()
@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000H\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001:\u0001\u0019B\u0013\b\u0007\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0012\u001a\u00020\u0013H\u0007J\u0012\u0010\u0014\u001a\u0004\u0018\u00010\u00152\u0006\u0010\u0016\u001a\u00020\u0017H\u0007J\u0006\u0010\u0018\u001a\u00020\u0017R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u000e\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\r0\f0\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u001a"}, d2 = {"Lio/arvo/dataconso/TelephonyRepository;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "subscriptionManager", "Landroid/telephony/SubscriptionManager;", "telephonyManager", "Landroid/telephony/TelephonyManager;", "_activeSubscriptions", "Lkotlinx/coroutines/flow/MutableStateFlow;", "", "Lio/arvo/dataconso/TelephonyRepository$ArvoSimInfo;", "activeSubscriptions", "Lkotlinx/coroutines/flow/StateFlow;", "getActiveSubscriptions", "()Lkotlinx/coroutines/flow/StateFlow;", "refreshSimInfo", "", "getSubscriberIdForSim", "", "slotIndex", "", "getDefaultDataSubId", "ArvoSimInfo", "app_debug"})
public final class TelephonyRepository {
    @org.jetbrains.annotations.NotNull()
    private final android.content.Context context = null;
    @org.jetbrains.annotations.NotNull()
    private final android.telephony.SubscriptionManager subscriptionManager = null;
    @org.jetbrains.annotations.NotNull()
    private final android.telephony.TelephonyManager telephonyManager = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.MutableStateFlow<java.util.List<io.arvo.dataconso.TelephonyRepository.ArvoSimInfo>> _activeSubscriptions = null;
    @org.jetbrains.annotations.NotNull()
    private final kotlinx.coroutines.flow.StateFlow<java.util.List<io.arvo.dataconso.TelephonyRepository.ArvoSimInfo>> activeSubscriptions = null;
    
    @javax.inject.Inject()
    public TelephonyRepository(@dagger.hilt.android.qualifiers.ApplicationContext()
    @org.jetbrains.annotations.NotNull()
    android.content.Context context) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final kotlinx.coroutines.flow.StateFlow<java.util.List<io.arvo.dataconso.TelephonyRepository.ArvoSimInfo>> getActiveSubscriptions() {
        return null;
    }
    
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    public final void refreshSimInfo() {
    }
    
    /**
     * Récupère le SubscriberId (IMSI) pour une SIM spécifique si la permission est présente.
     * Note: Sur Android 10+, renvoie souvent null pour les apps tierces, on utilise alors le SubId.
     */
    @android.annotation.SuppressLint(value = {"MissingPermission"})
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getSubscriberIdForSim(int slotIndex) {
        return null;
    }
    
    public final int getDefaultDataSubId() {
        return 0;
    }
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0015\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\u0006\u00a2\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\bH\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0006H\u00c6\u0003J;\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\b2\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u001c\u001a\u00020\u0006H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\rR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0011R\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0010\u00a8\u0006\u001d"}, d2 = {"Lio/arvo/dataconso/TelephonyRepository$ArvoSimInfo;", "", "subscriptionId", "", "slotIndex", "carrierName", "", "isDefaultData", "", "displayName", "<init>", "(IILjava/lang/String;ZLjava/lang/String;)V", "getSubscriptionId", "()I", "getSlotIndex", "getCarrierName", "()Ljava/lang/String;", "()Z", "getDisplayName", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "other", "hashCode", "toString", "app_debug"})
    public static final class ArvoSimInfo {
        private final int subscriptionId = 0;
        private final int slotIndex = 0;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String carrierName = null;
        private final boolean isDefaultData = false;
        @org.jetbrains.annotations.NotNull()
        private final java.lang.String displayName = null;
        
        public ArvoSimInfo(int subscriptionId, int slotIndex, @org.jetbrains.annotations.NotNull()
        java.lang.String carrierName, boolean isDefaultData, @org.jetbrains.annotations.NotNull()
        java.lang.String displayName) {
            super();
        }
        
        public final int getSubscriptionId() {
            return 0;
        }
        
        public final int getSlotIndex() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getCarrierName() {
            return null;
        }
        
        public final boolean isDefaultData() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String getDisplayName() {
            return null;
        }
        
        public final int component1() {
            return 0;
        }
        
        public final int component2() {
            return 0;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component3() {
            return null;
        }
        
        public final boolean component4() {
            return false;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final java.lang.String component5() {
            return null;
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.TelephonyRepository.ArvoSimInfo copy(int subscriptionId, int slotIndex, @org.jetbrains.annotations.NotNull()
        java.lang.String carrierName, boolean isDefaultData, @org.jetbrains.annotations.NotNull()
        java.lang.String displayName) {
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