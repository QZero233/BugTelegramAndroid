package com.qzero.telegram.dao;

import android.content.Context;

import com.qzero.telegram.dao.gen.DaoMaster;
import com.qzero.telegram.dao.gen.DaoSession;

import org.greenrobot.greendao.database.Database;

public class SessionManager {

    private Context context;

    private static SessionManager instance;

    //private DaoMaster.DevOpenHelper devOpenHelper;
    private DaoMaster.OpenHelper openHelper;


    private Database database;
    private DaoSession session;

    public static SessionManager getInstance(Context context) {
        if(instance==null)
            instance=new SessionManager(context);
        return instance;
    }

    private SessionManager(Context context) {
        this.context = context;
        initDao();
    }

    public void rebuildSession(){
        initDao();
    }

    private void initDao(){
        //devOpenHelper=new DaoMaster.DevOpenHelper(context,"localDatabase");
        openHelper=new MyOpenHelper(context,"localDatabase");
        database=openHelper.getWritableDb();
        session=new DaoMaster(database).newSession();
    }

    public DaoSession getSession() {
        return session;
    }
}
