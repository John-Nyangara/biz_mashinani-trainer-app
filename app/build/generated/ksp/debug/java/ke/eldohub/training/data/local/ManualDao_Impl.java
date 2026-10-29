package ke.eldohub.training.data.local;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.EntityUpsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ManualDao_Impl implements ManualDao {
  private final RoomDatabase __db;

  private final SharedSQLiteStatement __preparedStmtOfUpdateLastViewed;

  private final EntityUpsertionAdapter<ManualEntity> __upsertionAdapterOfManualEntity;

  public ManualDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__preparedStmtOfUpdateLastViewed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE manuals SET lastViewedAt = ? WHERE id = ?";
        return _query;
      }
    };
    this.__upsertionAdapterOfManualEntity = new EntityUpsertionAdapter<ManualEntity>(new EntityInsertionAdapter<ManualEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT INTO `manuals` (`id`,`title`,`version`,`assetPath`,`filePath`,`remoteUrl`,`textContent`,`lastViewedAt`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ManualEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getVersion());
        if (entity.getAssetPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAssetPath());
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getFilePath());
        }
        if (entity.getRemoteUrl() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRemoteUrl());
        }
        if (entity.getTextContent() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getTextContent());
        }
        if (entity.getLastViewedAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastViewedAt());
        }
      }
    }, new EntityDeletionOrUpdateAdapter<ManualEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE `manuals` SET `id` = ?,`title` = ?,`version` = ?,`assetPath` = ?,`filePath` = ?,`remoteUrl` = ?,`textContent` = ?,`lastViewedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ManualEntity entity) {
        statement.bindString(1, entity.getId());
        statement.bindString(2, entity.getTitle());
        statement.bindLong(3, entity.getVersion());
        if (entity.getAssetPath() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getAssetPath());
        }
        if (entity.getFilePath() == null) {
          statement.bindNull(5);
        } else {
          statement.bindString(5, entity.getFilePath());
        }
        if (entity.getRemoteUrl() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getRemoteUrl());
        }
        if (entity.getTextContent() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getTextContent());
        }
        if (entity.getLastViewedAt() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getLastViewedAt());
        }
        statement.bindString(9, entity.getId());
      }
    });
  }

  @Override
  public Object updateLastViewed(final String id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateLastViewed.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfUpdateLastViewed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object upsertAll(final List<ManualEntity> manuals,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfManualEntity.upsert(manuals);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object upsert(final ManualEntity manual, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __upsertionAdapterOfManualEntity.upsert(manual);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ManualSummary>> observeSummaries() {
    final String _sql = "SELECT id, title, version, assetPath, filePath, remoteUrl, lastViewedAt FROM manuals ORDER BY title ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"manuals"}, new Callable<List<ManualSummary>>() {
      @Override
      @NonNull
      public List<ManualSummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfTitle = 1;
          final int _cursorIndexOfVersion = 2;
          final int _cursorIndexOfAssetPath = 3;
          final int _cursorIndexOfFilePath = 4;
          final int _cursorIndexOfRemoteUrl = 5;
          final int _cursorIndexOfLastViewedAt = 6;
          final List<ManualSummary> _result = new ArrayList<ManualSummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ManualSummary _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            final String _tmpAssetPath;
            if (_cursor.isNull(_cursorIndexOfAssetPath)) {
              _tmpAssetPath = null;
            } else {
              _tmpAssetPath = _cursor.getString(_cursorIndexOfAssetPath);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpRemoteUrl;
            if (_cursor.isNull(_cursorIndexOfRemoteUrl)) {
              _tmpRemoteUrl = null;
            } else {
              _tmpRemoteUrl = _cursor.getString(_cursorIndexOfRemoteUrl);
            }
            final Long _tmpLastViewedAt;
            if (_cursor.isNull(_cursorIndexOfLastViewedAt)) {
              _tmpLastViewedAt = null;
            } else {
              _tmpLastViewedAt = _cursor.getLong(_cursorIndexOfLastViewedAt);
            }
            _item = new ManualSummary(_tmpId,_tmpTitle,_tmpVersion,_tmpAssetPath,_tmpFilePath,_tmpRemoteUrl,_tmpLastViewedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ManualEntity>> observeAll() {
    final String _sql = "SELECT * FROM manuals ORDER BY title ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"manuals"}, new Callable<List<ManualEntity>>() {
      @Override
      @NonNull
      public List<ManualEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfAssetPath = CursorUtil.getColumnIndexOrThrow(_cursor, "assetPath");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfRemoteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteUrl");
          final int _cursorIndexOfTextContent = CursorUtil.getColumnIndexOrThrow(_cursor, "textContent");
          final int _cursorIndexOfLastViewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastViewedAt");
          final List<ManualEntity> _result = new ArrayList<ManualEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ManualEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            final String _tmpAssetPath;
            if (_cursor.isNull(_cursorIndexOfAssetPath)) {
              _tmpAssetPath = null;
            } else {
              _tmpAssetPath = _cursor.getString(_cursorIndexOfAssetPath);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpRemoteUrl;
            if (_cursor.isNull(_cursorIndexOfRemoteUrl)) {
              _tmpRemoteUrl = null;
            } else {
              _tmpRemoteUrl = _cursor.getString(_cursorIndexOfRemoteUrl);
            }
            final String _tmpTextContent;
            if (_cursor.isNull(_cursorIndexOfTextContent)) {
              _tmpTextContent = null;
            } else {
              _tmpTextContent = _cursor.getString(_cursorIndexOfTextContent);
            }
            final Long _tmpLastViewedAt;
            if (_cursor.isNull(_cursorIndexOfLastViewedAt)) {
              _tmpLastViewedAt = null;
            } else {
              _tmpLastViewedAt = _cursor.getLong(_cursorIndexOfLastViewedAt);
            }
            _item = new ManualEntity(_tmpId,_tmpTitle,_tmpVersion,_tmpAssetPath,_tmpFilePath,_tmpRemoteUrl,_tmpTextContent,_tmpLastViewedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getById(final String id, final Continuation<? super ManualEntity> $completion) {
    final String _sql = "SELECT * FROM manuals WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<ManualEntity>() {
      @Override
      @Nullable
      public ManualEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfAssetPath = CursorUtil.getColumnIndexOrThrow(_cursor, "assetPath");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfRemoteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteUrl");
          final int _cursorIndexOfTextContent = CursorUtil.getColumnIndexOrThrow(_cursor, "textContent");
          final int _cursorIndexOfLastViewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastViewedAt");
          final ManualEntity _result;
          if (_cursor.moveToFirst()) {
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            final String _tmpAssetPath;
            if (_cursor.isNull(_cursorIndexOfAssetPath)) {
              _tmpAssetPath = null;
            } else {
              _tmpAssetPath = _cursor.getString(_cursorIndexOfAssetPath);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpRemoteUrl;
            if (_cursor.isNull(_cursorIndexOfRemoteUrl)) {
              _tmpRemoteUrl = null;
            } else {
              _tmpRemoteUrl = _cursor.getString(_cursorIndexOfRemoteUrl);
            }
            final String _tmpTextContent;
            if (_cursor.isNull(_cursorIndexOfTextContent)) {
              _tmpTextContent = null;
            } else {
              _tmpTextContent = _cursor.getString(_cursorIndexOfTextContent);
            }
            final Long _tmpLastViewedAt;
            if (_cursor.isNull(_cursorIndexOfLastViewedAt)) {
              _tmpLastViewedAt = null;
            } else {
              _tmpLastViewedAt = _cursor.getLong(_cursorIndexOfLastViewedAt);
            }
            _result = new ManualEntity(_tmpId,_tmpTitle,_tmpVersion,_tmpAssetPath,_tmpFilePath,_tmpRemoteUrl,_tmpTextContent,_tmpLastViewedAt);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ManualSummary>> observeRecentSummaries(final int limit) {
    final String _sql = "SELECT id, title, version, assetPath, filePath, remoteUrl, lastViewedAt FROM manuals WHERE lastViewedAt IS NOT NULL ORDER BY lastViewedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"manuals"}, new Callable<List<ManualSummary>>() {
      @Override
      @NonNull
      public List<ManualSummary> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = 0;
          final int _cursorIndexOfTitle = 1;
          final int _cursorIndexOfVersion = 2;
          final int _cursorIndexOfAssetPath = 3;
          final int _cursorIndexOfFilePath = 4;
          final int _cursorIndexOfRemoteUrl = 5;
          final int _cursorIndexOfLastViewedAt = 6;
          final List<ManualSummary> _result = new ArrayList<ManualSummary>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ManualSummary _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            final String _tmpAssetPath;
            if (_cursor.isNull(_cursorIndexOfAssetPath)) {
              _tmpAssetPath = null;
            } else {
              _tmpAssetPath = _cursor.getString(_cursorIndexOfAssetPath);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpRemoteUrl;
            if (_cursor.isNull(_cursorIndexOfRemoteUrl)) {
              _tmpRemoteUrl = null;
            } else {
              _tmpRemoteUrl = _cursor.getString(_cursorIndexOfRemoteUrl);
            }
            final Long _tmpLastViewedAt;
            if (_cursor.isNull(_cursorIndexOfLastViewedAt)) {
              _tmpLastViewedAt = null;
            } else {
              _tmpLastViewedAt = _cursor.getLong(_cursorIndexOfLastViewedAt);
            }
            _item = new ManualSummary(_tmpId,_tmpTitle,_tmpVersion,_tmpAssetPath,_tmpFilePath,_tmpRemoteUrl,_tmpLastViewedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ManualEntity>> observeRecentlyViewed(final int limit) {
    final String _sql = "SELECT * FROM manuals WHERE lastViewedAt IS NOT NULL ORDER BY lastViewedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"manuals"}, new Callable<List<ManualEntity>>() {
      @Override
      @NonNull
      public List<ManualEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfVersion = CursorUtil.getColumnIndexOrThrow(_cursor, "version");
          final int _cursorIndexOfAssetPath = CursorUtil.getColumnIndexOrThrow(_cursor, "assetPath");
          final int _cursorIndexOfFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "filePath");
          final int _cursorIndexOfRemoteUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "remoteUrl");
          final int _cursorIndexOfTextContent = CursorUtil.getColumnIndexOrThrow(_cursor, "textContent");
          final int _cursorIndexOfLastViewedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastViewedAt");
          final List<ManualEntity> _result = new ArrayList<ManualEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ManualEntity _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final String _tmpTitle;
            _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            final int _tmpVersion;
            _tmpVersion = _cursor.getInt(_cursorIndexOfVersion);
            final String _tmpAssetPath;
            if (_cursor.isNull(_cursorIndexOfAssetPath)) {
              _tmpAssetPath = null;
            } else {
              _tmpAssetPath = _cursor.getString(_cursorIndexOfAssetPath);
            }
            final String _tmpFilePath;
            if (_cursor.isNull(_cursorIndexOfFilePath)) {
              _tmpFilePath = null;
            } else {
              _tmpFilePath = _cursor.getString(_cursorIndexOfFilePath);
            }
            final String _tmpRemoteUrl;
            if (_cursor.isNull(_cursorIndexOfRemoteUrl)) {
              _tmpRemoteUrl = null;
            } else {
              _tmpRemoteUrl = _cursor.getString(_cursorIndexOfRemoteUrl);
            }
            final String _tmpTextContent;
            if (_cursor.isNull(_cursorIndexOfTextContent)) {
              _tmpTextContent = null;
            } else {
              _tmpTextContent = _cursor.getString(_cursorIndexOfTextContent);
            }
            final Long _tmpLastViewedAt;
            if (_cursor.isNull(_cursorIndexOfLastViewedAt)) {
              _tmpLastViewedAt = null;
            } else {
              _tmpLastViewedAt = _cursor.getLong(_cursorIndexOfLastViewedAt);
            }
            _item = new ManualEntity(_tmpId,_tmpTitle,_tmpVersion,_tmpAssetPath,_tmpFilePath,_tmpRemoteUrl,_tmpTextContent,_tmpLastViewedAt);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
