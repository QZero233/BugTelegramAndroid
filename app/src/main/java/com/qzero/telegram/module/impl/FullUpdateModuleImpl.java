package com.qzero.telegram.module.impl;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.module.FullUpdateModule;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.bean.FullUpdateStatus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.BiFunction;

public class FullUpdateModuleImpl implements FullUpdateModule {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Context context;

    private LocalDataStorage localDataStorage;

    private SessionModule sessionModule;

    private MessageModule messageModule;

    public FullUpdateModuleImpl(Context context) {
        this.context = context;
        localDataStorage=new LocalDataStorageImpl(context);
        sessionModule=new SessionModuleImpl(context);
        messageModule=new MessageModuleImpl(context);
    }

    private int getCurrentVersionCode(){
        try {
            PackageManager packageManager=context.getPackageManager();
            PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionCode;
        }catch (Exception e){
            log.error("Can not get current version code",e);
            return -1;
        }
    }

    @Override
    public boolean checkIfNeedFullUpdate() {
        FullUpdateStatus fullUpdateStatus;
        try {
            fullUpdateStatus=localDataStorage.getObject(LocalDataStorage.NAME_FULL_UPDATE_STATUS,FullUpdateStatus.class);
        } catch (IOException e) {
            log.error("Failed to get fullUpdateStatus",e);
            return false;
        }

        if(fullUpdateStatus==null)
            return true;

        return fullUpdateStatus.getLastFullUpdateVersionCode()<FullUpdateStatus.FULL_UPDATE_NEEDED_VERSION_CODE;
    }

    private void updateLastFullUpdateVersionCode(int code){
        try {
            FullUpdateStatus fullUpdateStatus=localDataStorage.getObject(LocalDataStorage.NAME_FULL_UPDATE_STATUS,FullUpdateStatus.class);
            if(fullUpdateStatus==null)
                fullUpdateStatus=new FullUpdateStatus(0);
            fullUpdateStatus.setLastFullUpdateVersionCode(code);
            localDataStorage.storeObject(LocalDataStorage.NAME_FULL_UPDATE_STATUS,fullUpdateStatus);
        } catch (IOException e) {
            log.error("Failed to update fullUpdateStatus",e);
        }
    }

    @Override
    public void resetFullUpdateStatus() {
        updateLastFullUpdateVersionCode(-1);
    }

    @Override
    public void setUpdated() {
        updateLastFullUpdateVersionCode(getCurrentVersionCode());
    }

    @Override
    public Observable<Boolean> executeFullUpdate() {
        //Temporarily we full update session and message
        return sessionModule.getAllSessions()
                .flatMap(sessions -> Observable.fromIterable(sessions))
                .flatMap(chatSession -> messageModule.getAllMessagesBySessionIdRemotely(chatSession.getSessionId()))
                .flatMap(chatMessages -> Observable.just(true));
    }
}
