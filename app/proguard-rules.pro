# Preserve metadata used by reflection-based Android libraries.
-keepattributes Signature,*Annotation*,EnclosingMethod,InnerClasses

# Room maps database entities and database classes at runtime.
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Database class * { *; }
-keep class * extends androidx.room.RoomDatabase { *; }

# WorkManager resolves workers by their class name.
-keep class * extends androidx.work.ListenableWorker { *; }

# Keep app components that are also declared or resolved through Android APIs.
-keep class * extends android.appwidget.AppWidgetProvider { *; }
-keep class * extends android.net.VpnService { *; }

# SQLCipher contains native entry points.
-keep class net.sqlcipher.database.** { *; }

# Firebase and Compose include their own consumer R8 rules.
