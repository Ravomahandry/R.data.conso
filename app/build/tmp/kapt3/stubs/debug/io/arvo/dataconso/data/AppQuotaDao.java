package io.arvo.dataconso.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\bg\u0018\u00002\u00020\u0001J\u0014\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0005J\u0014\u0010\u0006\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00040\u00030\u0007H\'J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\u00020\t2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u000eJ\u0016\u0010\u000f\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u0004H\u00a7@\u00a2\u0006\u0002\u0010\u000bJ\u001e\u0010\u0010\u001a\u00020\t2\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u0015J\u0018\u0010\u0016\u001a\u0004\u0018\u00010\u00042\u0006\u0010\u0011\u001a\u00020\u0012H\u00a7@\u00a2\u0006\u0002\u0010\u0017J\u0016\u0010\u0018\u001a\u00020\t2\u0006\u0010\u0019\u001a\u00020\u0014H\u00a7@\u00a2\u0006\u0002\u0010\u001a\u00a8\u0006\u001b\u00c0\u0006\u0003"}, d2 = {"Lio/arvo/dataconso/data/AppQuotaDao;", "", "getAllQuotas", "", "Lio/arvo/dataconso/data/AppQuotaEntity;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAllQuotasFlow", "Lkotlinx/coroutines/flow/Flow;", "saveQuota", "", "quota", "(Lio/arvo/dataconso/data/AppQuotaEntity;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "saveAllQuotas", "quotas", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteQuota", "updateUsage", "packageName", "", "used", "", "(Ljava/lang/String;JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getQuotaForApp", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "resetAllQuotas", "now", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface AppQuotaDao {
    
    @androidx.room.Query(value = "SELECT * FROM app_quotas")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getAllQuotas(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super java.util.List<io.arvo.dataconso.data.AppQuotaEntity>> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM app_quotas")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<java.util.List<io.arvo.dataconso.data.AppQuotaEntity>> getAllQuotasFlow();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveQuota(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.data.AppQuotaEntity quota, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveAllQuotas(@org.jetbrains.annotations.NotNull()
    java.util.List<io.arvo.dataconso.data.AppQuotaEntity> quotas, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Delete()
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object deleteQuota(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.data.AppQuotaEntity quota, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "UPDATE app_quotas SET usedBytes = :used WHERE packageName = :packageName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object updateUsage(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, long used, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM app_quotas WHERE packageName = :packageName")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getQuotaForApp(@org.jetbrains.annotations.NotNull()
    java.lang.String packageName, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super io.arvo.dataconso.data.AppQuotaEntity> $completion);
    
    @androidx.room.Query(value = "UPDATE app_quotas SET isBlocked = 0, usedBytes = 0, lastResetTime = :now")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object resetAllQuotas(long now, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}