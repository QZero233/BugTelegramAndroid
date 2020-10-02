package com.qzero.telegram.dao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.qzero.telegram.dao.entity.ChatMemberConverter;
import java.util.List;

import com.qzero.telegram.dao.entity.ChatSession;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "CHAT_SESSION".
*/
public class ChatSessionDao extends AbstractDao<ChatSession, String> {

    public static final String TABLENAME = "CHAT_SESSION";

    /**
     * Properties of entity ChatSession.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property SessionId = new Property(0, String.class, "sessionId", true, "sessionId");
        public final static Property SessionName = new Property(1, String.class, "sessionName", false, "sessionName");
        public final static Property ChatMembers = new Property(2, String.class, "chatMembers", false, "chatMembers");
    }

    private final ChatMemberConverter chatMembersConverter = new ChatMemberConverter();

    public ChatSessionDao(DaoConfig config) {
        super(config);
    }
    
    public ChatSessionDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"CHAT_SESSION\" (" + //
                "\"sessionId\" TEXT PRIMARY KEY NOT NULL ," + // 0: sessionId
                "\"sessionName\" TEXT," + // 1: sessionName
                "\"chatMembers\" TEXT);"); // 2: chatMembers
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"CHAT_SESSION\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, ChatSession entity) {
        stmt.clearBindings();
 
        String sessionId = entity.getSessionId();
        if (sessionId != null) {
            stmt.bindString(1, sessionId);
        }
 
        String sessionName = entity.getSessionName();
        if (sessionName != null) {
            stmt.bindString(2, sessionName);
        }
 
        List chatMembers = entity.getChatMembers();
        if (chatMembers != null) {
            stmt.bindString(3, chatMembersConverter.convertToDatabaseValue(chatMembers));
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, ChatSession entity) {
        stmt.clearBindings();
 
        String sessionId = entity.getSessionId();
        if (sessionId != null) {
            stmt.bindString(1, sessionId);
        }
 
        String sessionName = entity.getSessionName();
        if (sessionName != null) {
            stmt.bindString(2, sessionName);
        }
 
        List chatMembers = entity.getChatMembers();
        if (chatMembers != null) {
            stmt.bindString(3, chatMembersConverter.convertToDatabaseValue(chatMembers));
        }
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public ChatSession readEntity(Cursor cursor, int offset) {
        ChatSession entity = new ChatSession( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // sessionId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // sessionName
            cursor.isNull(offset + 2) ? null : chatMembersConverter.convertToEntityProperty(cursor.getString(offset + 2)) // chatMembers
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, ChatSession entity, int offset) {
        entity.setSessionId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setSessionName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setChatMembers(cursor.isNull(offset + 2) ? null : chatMembersConverter.convertToEntityProperty(cursor.getString(offset + 2)));
     }
    
    @Override
    protected final String updateKeyAfterInsert(ChatSession entity, long rowId) {
        return entity.getSessionId();
    }
    
    @Override
    public String getKey(ChatSession entity) {
        if(entity != null) {
            return entity.getSessionId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(ChatSession entity) {
        return entity.getSessionId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}