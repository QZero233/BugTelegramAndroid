package com.qzero.telegram.dao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.qzero.telegram.dao.entity.FileResource;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "file_resource".
*/
public class FileResourceDao extends AbstractDao<FileResource, String> {

    public static final String TABLENAME = "file_resource";

    /**
     * Properties of entity FileResource.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property ResourceId = new Property(0, String.class, "resourceId", true, "resourceId");
        public final static Property ResourceName = new Property(1, String.class, "resourceName", false, "resourceName");
        public final static Property ResourceLength = new Property(2, Long.class, "resourceLength", false, "resourceLength");
        public final static Property ResourceStatus = new Property(3, int.class, "resourceStatus", false, "resourceStatus");
    }


    public FileResourceDao(DaoConfig config) {
        super(config);
    }
    
    public FileResourceDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"file_resource\" (" + //
                "\"resourceId\" TEXT PRIMARY KEY NOT NULL ," + // 0: resourceId
                "\"resourceName\" TEXT," + // 1: resourceName
                "\"resourceLength\" INTEGER," + // 2: resourceLength
                "\"resourceStatus\" INTEGER NOT NULL );"); // 3: resourceStatus
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"file_resource\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, FileResource entity) {
        stmt.clearBindings();
 
        String resourceId = entity.getResourceId();
        if (resourceId != null) {
            stmt.bindString(1, resourceId);
        }
 
        String resourceName = entity.getResourceName();
        if (resourceName != null) {
            stmt.bindString(2, resourceName);
        }
 
        Long resourceLength = entity.getResourceLength();
        if (resourceLength != null) {
            stmt.bindLong(3, resourceLength);
        }
        stmt.bindLong(4, entity.getResourceStatus());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, FileResource entity) {
        stmt.clearBindings();
 
        String resourceId = entity.getResourceId();
        if (resourceId != null) {
            stmt.bindString(1, resourceId);
        }
 
        String resourceName = entity.getResourceName();
        if (resourceName != null) {
            stmt.bindString(2, resourceName);
        }
 
        Long resourceLength = entity.getResourceLength();
        if (resourceLength != null) {
            stmt.bindLong(3, resourceLength);
        }
        stmt.bindLong(4, entity.getResourceStatus());
    }

    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0);
    }    

    @Override
    public FileResource readEntity(Cursor cursor, int offset) {
        FileResource entity = new FileResource( //
            cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0), // resourceId
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // resourceName
            cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2), // resourceLength
            cursor.getInt(offset + 3) // resourceStatus
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, FileResource entity, int offset) {
        entity.setResourceId(cursor.isNull(offset + 0) ? null : cursor.getString(offset + 0));
        entity.setResourceName(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setResourceLength(cursor.isNull(offset + 2) ? null : cursor.getLong(offset + 2));
        entity.setResourceStatus(cursor.getInt(offset + 3));
     }
    
    @Override
    protected final String updateKeyAfterInsert(FileResource entity, long rowId) {
        return entity.getResourceId();
    }
    
    @Override
    public String getKey(FileResource entity) {
        if(entity != null) {
            return entity.getResourceId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(FileResource entity) {
        return entity.getResourceId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
