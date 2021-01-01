package com.qzero.telegram.dao;

import android.content.Context;

import com.qzero.telegram.dao.gen.ChatMemberDao;
import com.qzero.telegram.dao.gen.ChatMessageDao;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.dao.gen.ChatSessionParameterDao;
import com.qzero.telegram.dao.gen.DaoMaster;
import com.qzero.telegram.dao.gen.UserInfoDao;

import org.greenrobot.greendao.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyOpenHelper extends DaoMaster.OpenHelper {

    private Logger log= LoggerFactory.getLogger(getClass());

    public MyOpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }
            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, ChatMemberDao.class, ChatMessageDao.class, ChatSessionDao.class, ChatSessionParameterDao.class, UserInfoDao.class);
        log.debug(String.format("Database update finished from %d to %d", oldVersion,newVersion));
    }
}
