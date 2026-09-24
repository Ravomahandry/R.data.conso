package io.arvo.dataconso.data;

@kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&\u00a8\u0006\u000b"}, d2 = {"Lio/arvo/dataconso/data/AppDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "settingsDao", "Lio/arvo/dataconso/data/SettingsDao;", "historyDao", "Lio/arvo/dataconso/data/HistoryDao;", "quotaDao", "Lio/arvo/dataconso/data/AppQuotaDao;", "Companion", "app_debug"})
@androidx.room.Database(entities = {io.arvo.dataconso.data.AppSettings.class, io.arvo.dataconso.data.HistoryEntry.class, io.arvo.dataconso.data.SimulationEntry.class, io.arvo.dataconso.data.AppQuotaEntity.class}, version = 32, exportSchema = false)
public abstract class AppDatabase extends androidx.room.RoomDatabase {
    @org.jetbrains.annotations.NotNull()
    private static final java.lang.String DB_NAME = "arvo_v5_final.db";
    @kotlin.jvm.Volatile()
    @org.jetbrains.annotations.Nullable()
    private static volatile io.arvo.dataconso.data.AppDatabase INSTANCE;
    @org.jetbrains.annotations.NotNull()
    private static final androidx.room.migration.Migration MIGRATION_31_32 = null;
    @org.jetbrains.annotations.NotNull()
    public static final io.arvo.dataconso.data.AppDatabase.Companion Companion = null;
    
    public AppDatabase() {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public abstract io.arvo.dataconso.data.SettingsDao settingsDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract io.arvo.dataconso.data.HistoryDao historyDao();
    
    @org.jetbrains.annotations.NotNull()
    public abstract io.arvo.dataconso.data.AppQuotaDao quotaDao();
    
    @kotlin.Metadata(mv = {2, 2, 0}, k = 1, xi = 48, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u000b\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rJ\u0010\u0010\u000e\u001a\u00020\u00072\u0006\u0010\f\u001a\u00020\rH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\n\u00a8\u0006\u000f"}, d2 = {"Lio/arvo/dataconso/data/AppDatabase$Companion;", "", "<init>", "()V", "DB_NAME", "", "INSTANCE", "Lio/arvo/dataconso/data/AppDatabase;", "MIGRATION_31_32", "Landroidx/room/migration/Migration;", "Landroidx/room/migration/Migration;", "getDatabase", "context", "Landroid/content/Context;", "buildDatabase", "app_debug"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
        
        @org.jetbrains.annotations.NotNull()
        public final io.arvo.dataconso.data.AppDatabase getDatabase(@org.jetbrains.annotations.NotNull()
        android.content.Context context) {
            return null;
        }
        
        private final io.arvo.dataconso.data.AppDatabase buildDatabase(android.content.Context context) {
            return null;
        }
    }
}