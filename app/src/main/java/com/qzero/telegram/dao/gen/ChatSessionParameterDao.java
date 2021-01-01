package com.qzero.telegram.dao.gen;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.qzero.telegram.dao.entity.ChatSessionParameter;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAT_SESSION_PARAMETER".
*/
public class ChatSessionParameterDao extends AbstractDao<ChatSessionParameter, Long> {

    public static final String TABLENAME = "CHAT_SESSION_PARAMETER";

    /**
     * Properties of entity ChatSessionParameter.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ParameterId = new Property(0, Long.class, "parameterId", true, "_id");
        public final static Property SessionId = new Property(1, String.class, "sessionId", false, "sessionId");
        public final static Property ParameterName = new Property(2, String.class, "parameterName", false, "parameterName");
        public final static Property ParameterValue = new Property(3, String.class, "parameterValue", false, "parameterValue");
    }

    private Query<ChatSessionParameter> chatSession_SessionParametersQuery;

    public ChatSessionParameterDao(DaoConfig config) {
        super(config);
    }
    
    public ChatSessionParameterDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_SESSION_PARAMETER\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: parameterId
                "\"sessionId\" TEXT," + // 1: sessionId
                "\"parameterName\" TEXT," + // 2: parameterName
                "\"parameterValue\" TEXT);"); // 3: parameterValue
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_SESSION_PARAMETER\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChatSessionParameter entity) {
        stmt.clearBindings();
 
        Long parameterId = entity.getParameterId();
        if (parameterId != null) {
            stmt.bindLong(1, parameterId);
        }
 
        String sessionId = entity.getSessionId();
        if (sessionId != null) {
            stmt.bindString(2, sessionId);
        }
 
        String parameterName = entity.getParameterName();
        if (parameterName != null) {
            stmt.bindString(3, parameterName);
        }
 
        String parameterValue = entity.getParameterValue();
        if (parameterValue != null) {
            stmt.bindString(4, parameterValue);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChatSessionParameter entity) {
        stmt.clearBindings();
 
        Long parameterId = entity.getParameterId();
        if (parameterId != null) {
            stmt.bindLong(1, parameterId);
        }
 
        String sessionId = entity.getSessionId();
        if (sessionId != null) {
            stmt.bindString(2, sessionId);
        }
 
        String parameterName = entity.getParameterName();
        if (parameterName != null) {
            stmt.bindString(3, parameterName);
        }
 
        String parameterValue = entity.getParameterValue();
        if (parameterValue != null) {
            stmt.bindString(4, parameterValue);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public ChatSessionParameter readEntity(Cursor cursor, int offset) {
        ChatSessionParameter entity = new ChatSessionParameter( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // parameterId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sessionId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // parameterName
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3) // parameterValue
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChatSessionParameter entity, int offset) {
        entity.setParameterId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setSessionId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setParameterName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setParameterValue(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(ChatSessionParameter entity, long rowId) {
        entity.setParameterId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(ChatSessionParameter entity) {
        if(entity != null) {
            return entity.getParameterId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChatSessionParameter entity) {
        return entity.getParameterId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "sessionParameters" to-many relationship of ChatSession. */
    public List<ChatSessionParameter> _queryChatSession_SessionParameters(String sessionId) {
        synchronized (this) {
            if (chatSession_SessionParametersQuery == null) {
                QueryBuilder<ChatSessionParameter> queryBuilder = queryBuilder();
                queryBuilder.where(Properties.SessionId.eq(null));
                chatSession_SessionParametersQuery = queryBuilder.build();
            }
        }
        Query<ChatSessionParameter> query = chatSession_SessionParametersQuery.forCurrentThread();
        query.setParameter(0, sessionId);
        return query.list();
    }

}
