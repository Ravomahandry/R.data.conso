package io.arvo.dataconso.`data`

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AppQuotaDao_Impl(
  __db: RoomDatabase,
) : AppQuotaDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAppQuotaEntity: EntityInsertAdapter<AppQuotaEntity>

  private val __deleteAdapterOfAppQuotaEntity: EntityDeleteOrUpdateAdapter<AppQuotaEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAppQuotaEntity = object : EntityInsertAdapter<AppQuotaEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `app_quotas` (`packageName`,`appName`,`quotaBytes`,`usedBytes`,`isEnabled`,`isBlocked`,`isManualBlocked`,`lastResetTime`,`networkType`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AppQuotaEntity) {
        statement.bindText(1, entity.packageName)
        statement.bindText(2, entity.appName)
        statement.bindLong(3, entity.quotaBytes)
        statement.bindLong(4, entity.usedBytes)
        val _tmp: Int = if (entity.isEnabled) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmp_1: Int = if (entity.isBlocked) 1 else 0
        statement.bindLong(6, _tmp_1.toLong())
        val _tmp_2: Int = if (entity.isManualBlocked) 1 else 0
        statement.bindLong(7, _tmp_2.toLong())
        statement.bindLong(8, entity.lastResetTime)
        statement.bindText(9, entity.networkType)
      }
    }
    this.__deleteAdapterOfAppQuotaEntity = object : EntityDeleteOrUpdateAdapter<AppQuotaEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `app_quotas` WHERE `packageName` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: AppQuotaEntity) {
        statement.bindText(1, entity.packageName)
      }
    }
  }

  public override suspend fun saveQuota(quota: AppQuotaEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfAppQuotaEntity.insert(_connection, quota)
  }

  public override suspend fun saveAllQuotas(quotas: List<AppQuotaEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAppQuotaEntity.insert(_connection, quotas)
  }

  public override suspend fun deleteQuota(quota: AppQuotaEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfAppQuotaEntity.handle(_connection, quota)
  }

  public override suspend fun getAllQuotas(): List<AppQuotaEntity> {
    val _sql: String = "SELECT * FROM app_quotas"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPackageName: Int = getColumnIndexOrThrow(_stmt, "packageName")
        val _columnIndexOfAppName: Int = getColumnIndexOrThrow(_stmt, "appName")
        val _columnIndexOfQuotaBytes: Int = getColumnIndexOrThrow(_stmt, "quotaBytes")
        val _columnIndexOfUsedBytes: Int = getColumnIndexOrThrow(_stmt, "usedBytes")
        val _columnIndexOfIsEnabled: Int = getColumnIndexOrThrow(_stmt, "isEnabled")
        val _columnIndexOfIsBlocked: Int = getColumnIndexOrThrow(_stmt, "isBlocked")
        val _columnIndexOfIsManualBlocked: Int = getColumnIndexOrThrow(_stmt, "isManualBlocked")
        val _columnIndexOfLastResetTime: Int = getColumnIndexOrThrow(_stmt, "lastResetTime")
        val _columnIndexOfNetworkType: Int = getColumnIndexOrThrow(_stmt, "networkType")
        val _result: MutableList<AppQuotaEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AppQuotaEntity
          val _tmpPackageName: String
          _tmpPackageName = _stmt.getText(_columnIndexOfPackageName)
          val _tmpAppName: String
          _tmpAppName = _stmt.getText(_columnIndexOfAppName)
          val _tmpQuotaBytes: Long
          _tmpQuotaBytes = _stmt.getLong(_columnIndexOfQuotaBytes)
          val _tmpUsedBytes: Long
          _tmpUsedBytes = _stmt.getLong(_columnIndexOfUsedBytes)
          val _tmpIsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEnabled).toInt()
          _tmpIsEnabled = _tmp != 0
          val _tmpIsBlocked: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBlocked).toInt()
          _tmpIsBlocked = _tmp_1 != 0
          val _tmpIsManualBlocked: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsManualBlocked).toInt()
          _tmpIsManualBlocked = _tmp_2 != 0
          val _tmpLastResetTime: Long
          _tmpLastResetTime = _stmt.getLong(_columnIndexOfLastResetTime)
          val _tmpNetworkType: String
          _tmpNetworkType = _stmt.getText(_columnIndexOfNetworkType)
          _item =
              AppQuotaEntity(_tmpPackageName,_tmpAppName,_tmpQuotaBytes,_tmpUsedBytes,_tmpIsEnabled,_tmpIsBlocked,_tmpIsManualBlocked,_tmpLastResetTime,_tmpNetworkType)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllQuotasFlow(): Flow<List<AppQuotaEntity>> {
    val _sql: String = "SELECT * FROM app_quotas"
    return createFlow(__db, false, arrayOf("app_quotas")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPackageName: Int = getColumnIndexOrThrow(_stmt, "packageName")
        val _columnIndexOfAppName: Int = getColumnIndexOrThrow(_stmt, "appName")
        val _columnIndexOfQuotaBytes: Int = getColumnIndexOrThrow(_stmt, "quotaBytes")
        val _columnIndexOfUsedBytes: Int = getColumnIndexOrThrow(_stmt, "usedBytes")
        val _columnIndexOfIsEnabled: Int = getColumnIndexOrThrow(_stmt, "isEnabled")
        val _columnIndexOfIsBlocked: Int = getColumnIndexOrThrow(_stmt, "isBlocked")
        val _columnIndexOfIsManualBlocked: Int = getColumnIndexOrThrow(_stmt, "isManualBlocked")
        val _columnIndexOfLastResetTime: Int = getColumnIndexOrThrow(_stmt, "lastResetTime")
        val _columnIndexOfNetworkType: Int = getColumnIndexOrThrow(_stmt, "networkType")
        val _result: MutableList<AppQuotaEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AppQuotaEntity
          val _tmpPackageName: String
          _tmpPackageName = _stmt.getText(_columnIndexOfPackageName)
          val _tmpAppName: String
          _tmpAppName = _stmt.getText(_columnIndexOfAppName)
          val _tmpQuotaBytes: Long
          _tmpQuotaBytes = _stmt.getLong(_columnIndexOfQuotaBytes)
          val _tmpUsedBytes: Long
          _tmpUsedBytes = _stmt.getLong(_columnIndexOfUsedBytes)
          val _tmpIsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEnabled).toInt()
          _tmpIsEnabled = _tmp != 0
          val _tmpIsBlocked: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBlocked).toInt()
          _tmpIsBlocked = _tmp_1 != 0
          val _tmpIsManualBlocked: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsManualBlocked).toInt()
          _tmpIsManualBlocked = _tmp_2 != 0
          val _tmpLastResetTime: Long
          _tmpLastResetTime = _stmt.getLong(_columnIndexOfLastResetTime)
          val _tmpNetworkType: String
          _tmpNetworkType = _stmt.getText(_columnIndexOfNetworkType)
          _item =
              AppQuotaEntity(_tmpPackageName,_tmpAppName,_tmpQuotaBytes,_tmpUsedBytes,_tmpIsEnabled,_tmpIsBlocked,_tmpIsManualBlocked,_tmpLastResetTime,_tmpNetworkType)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getQuotaForApp(packageName: String): AppQuotaEntity? {
    val _sql: String = "SELECT * FROM app_quotas WHERE packageName = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, packageName)
        val _columnIndexOfPackageName: Int = getColumnIndexOrThrow(_stmt, "packageName")
        val _columnIndexOfAppName: Int = getColumnIndexOrThrow(_stmt, "appName")
        val _columnIndexOfQuotaBytes: Int = getColumnIndexOrThrow(_stmt, "quotaBytes")
        val _columnIndexOfUsedBytes: Int = getColumnIndexOrThrow(_stmt, "usedBytes")
        val _columnIndexOfIsEnabled: Int = getColumnIndexOrThrow(_stmt, "isEnabled")
        val _columnIndexOfIsBlocked: Int = getColumnIndexOrThrow(_stmt, "isBlocked")
        val _columnIndexOfIsManualBlocked: Int = getColumnIndexOrThrow(_stmt, "isManualBlocked")
        val _columnIndexOfLastResetTime: Int = getColumnIndexOrThrow(_stmt, "lastResetTime")
        val _columnIndexOfNetworkType: Int = getColumnIndexOrThrow(_stmt, "networkType")
        val _result: AppQuotaEntity?
        if (_stmt.step()) {
          val _tmpPackageName: String
          _tmpPackageName = _stmt.getText(_columnIndexOfPackageName)
          val _tmpAppName: String
          _tmpAppName = _stmt.getText(_columnIndexOfAppName)
          val _tmpQuotaBytes: Long
          _tmpQuotaBytes = _stmt.getLong(_columnIndexOfQuotaBytes)
          val _tmpUsedBytes: Long
          _tmpUsedBytes = _stmt.getLong(_columnIndexOfUsedBytes)
          val _tmpIsEnabled: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsEnabled).toInt()
          _tmpIsEnabled = _tmp != 0
          val _tmpIsBlocked: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBlocked).toInt()
          _tmpIsBlocked = _tmp_1 != 0
          val _tmpIsManualBlocked: Boolean
          val _tmp_2: Int
          _tmp_2 = _stmt.getLong(_columnIndexOfIsManualBlocked).toInt()
          _tmpIsManualBlocked = _tmp_2 != 0
          val _tmpLastResetTime: Long
          _tmpLastResetTime = _stmt.getLong(_columnIndexOfLastResetTime)
          val _tmpNetworkType: String
          _tmpNetworkType = _stmt.getText(_columnIndexOfNetworkType)
          _result =
              AppQuotaEntity(_tmpPackageName,_tmpAppName,_tmpQuotaBytes,_tmpUsedBytes,_tmpIsEnabled,_tmpIsBlocked,_tmpIsManualBlocked,_tmpLastResetTime,_tmpNetworkType)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateUsage(packageName: String, used: Long) {
    val _sql: String = "UPDATE app_quotas SET usedBytes = ? WHERE packageName = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, used)
        _argIndex = 2
        _stmt.bindText(_argIndex, packageName)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun resetAllQuotas(now: Long) {
    val _sql: String = "UPDATE app_quotas SET isBlocked = 0, usedBytes = 0, lastResetTime = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
