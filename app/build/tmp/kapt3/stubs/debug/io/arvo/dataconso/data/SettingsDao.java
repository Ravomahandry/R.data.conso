package io.arvo.dataconso.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\bg\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003H\u00a7@\u00a2\u0006\u0002\u0010\u0004J\u0010\u0010\u0005\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00030\u0006H\'J\u0016\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0003H\u00a7@\u00a2\u0006\u0002\u0010\n\u00a8\u0006\u000b\u00c0\u0006\u0003"}, d2 = {"Lio/arvo/dataconso/data/SettingsDao;", "", "getSettings", "Lio/arvo/dataconso/data/AppSettings;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getSettingsFlow", "Lkotlinx/coroutines/flow/Flow;", "saveSettings", "", "settings", "(Lio/arvo/dataconso/data/AppSettings;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "app_debug"})
@androidx.room.Dao()
public abstract interface SettingsDao {
    
    @androidx.room.Query(value = "SELECT * FROM app_settings WHERE id = 1")
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object getSettings(@org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super io.arvo.dataconso.data.AppSettings> $completion);
    
    @androidx.room.Query(value = "SELECT * FROM app_settings WHERE id = 1")
    @org.jetbrains.annotations.NotNull()
    public abstract kotlinx.coroutines.flow.Flow<io.arvo.dataconso.data.AppSettings> getSettingsFlow();
    
    @androidx.room.Insert(onConflict = 1)
    @org.jetbrains.annotations.Nullable()
    public abstract java.lang.Object saveSettings(@org.jetbrains.annotations.NotNull()
    io.arvo.dataconso.data.AppSettings settings, @org.jetbrains.annotations.NotNull()
    kotlin.coroutines.Continuation<? super kotlin.Unit> $completion);
}