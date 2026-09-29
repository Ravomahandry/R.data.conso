package io.arvo.dataconso.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "hotspot_sessions",
    indices = [
        Index(value = ["startTimestamp"]),
        Index(value = ["sessionId"], unique = true)
    ]
)
data class HotspotSessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val startTimestamp: Long,
    @ColumnInfo(defaultValue = "0") val endTimestamp: Long = 0,
    @ColumnInfo(defaultValue = "0") val durationMillis: Long = 0,
    @ColumnInfo(defaultValue = "0") val rxBytes: Long = 0,
    @ColumnInfo(defaultValue = "0") val txBytes: Long = 0,
    @ColumnInfo(defaultValue = "0") val totalBytes: Long = 0,
    @ColumnInfo(defaultValue = "0") val synced: Boolean = false,
    @ColumnInfo(defaultValue = "0") val lastSyncedTimestamp: Long = 0,
    @ColumnInfo(defaultValue = "''") val sessionId: String,
    @ColumnInfo(defaultValue = "0") val baselineRxBytes: Long = 0,
    @ColumnInfo(defaultValue = "0") val baselineTxBytes: Long = 0,
    @ColumnInfo(defaultValue = "0") val lastRxBytes: Long = 0,
    @ColumnInfo(defaultValue = "0") val lastTxBytes: Long = 0
) {
    val isActive: Boolean
        get() = endTimestamp == 0L
}
