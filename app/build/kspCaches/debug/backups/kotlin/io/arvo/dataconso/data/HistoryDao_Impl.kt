package io.arvo.dataconso.`data`

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class HistoryDao_Impl(
  __db: RoomDatabase,
) : HistoryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHistoryEntry: EntityInsertAdapter<HistoryEntry>
  init {
    this.__db = __db
    this.__insertAdapterOfHistoryEntry = object : EntityInsertAdapter<HistoryEntry>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `history` (`timestamp`,`dateLabel`,`simId`,`bytes`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HistoryEntry) {
        statement.bindLong(1, entity.timestamp)
        statement.bindText(2, entity.dateLabel)
        statement.bindText(3, entity.simId)
        statement.bindLong(4, entity.bytes)
      }
    }
  }

  public override suspend fun insert(entry: HistoryEntry): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfHistoryEntry.insert(_connection, entry)
  }

  public override suspend fun insertAll(entries: List<HistoryEntry>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfHistoryEntry.insert(_connection, entries)
  }

  public override suspend fun getAll(): List<HistoryEntry> {
    val _sql: String = "SELECT * FROM history ORDER BY timestamp DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfDateLabel: Int = getColumnIndexOrThrow(_stmt, "dateLabel")
        val _columnIndexOfSimId: Int = getColumnIndexOrThrow(_stmt, "simId")
        val _columnIndexOfBytes: Int = getColumnIndexOrThrow(_stmt, "bytes")
        val _result: MutableList<HistoryEntry> = mutableListOf()
        while (_stmt.step()) {
          val _item: HistoryEntry
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpDateLabel: String
          _tmpDateLabel = _stmt.getText(_columnIndexOfDateLabel)
          val _tmpSimId: String
          _tmpSimId = _stmt.getText(_columnIndexOfSimId)
          val _tmpBytes: Long
          _tmpBytes = _stmt.getLong(_columnIndexOfBytes)
          _item = HistoryEntry(_tmpTimestamp,_tmpDateLabel,_tmpSimId,_tmpBytes)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getEntryByDateAndSim(date: String, sim: String): HistoryEntry? {
    val _sql: String = "SELECT * FROM history WHERE dateLabel = ? AND simId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, date)
        _argIndex = 2
        _stmt.bindText(_argIndex, sim)
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _columnIndexOfDateLabel: Int = getColumnIndexOrThrow(_stmt, "dateLabel")
        val _columnIndexOfSimId: Int = getColumnIndexOrThrow(_stmt, "simId")
        val _columnIndexOfBytes: Int = getColumnIndexOrThrow(_stmt, "bytes")
        val _result: HistoryEntry?
        if (_stmt.step()) {
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          val _tmpDateLabel: String
          _tmpDateLabel = _stmt.getText(_columnIndexOfDateLabel)
          val _tmpSimId: String
          _tmpSimId = _stmt.getText(_columnIndexOfSimId)
          val _tmpBytes: Long
          _tmpBytes = _stmt.getLong(_columnIndexOfBytes)
          _result = HistoryEntry(_tmpTimestamp,_tmpDateLabel,_tmpSimId,_tmpBytes)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
