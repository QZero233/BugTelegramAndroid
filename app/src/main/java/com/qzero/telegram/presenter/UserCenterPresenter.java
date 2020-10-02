package com.qzero.telegram.presenter;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.UserCenterContract;
import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.bean.FullUpdateStatus;
import com.qzero.telegram.module.impl.SessionModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;


public class UserCenterPresenter extends BasePresenter<UserCenterContract.View> implements UserCenterContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Context context;

    @Override
    public void attachView(@NonNull UserCenterContract.View mView) {
        super.attachView(mView);

        context=mView.getContext();
    }

    private int getVersionCode(){
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(),PackageManager.GET_CONFIGURATIONS);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            log.error("",e);
            return -1;
        }
    }

    @Override
    public void checkFullUpdateStatus() {
        LocalDataStorage localDataStorage=new LocalDataStorageImpl(context);
        FullUpdateStatus fullUpdateStatus=null;
        try {
            fullUpdateStatus=localDataStorage.getObject(LocalDataStorage.NAME_FULL_UPDATE_STATUS,FullUpdateStatus.class);
        }catch (IOException e){
            log.error("Failed to get fullUpdateStatus",e);
            getView().showToast("Failed to get update status,you can try to manual update by using settings");
        }

        if(fullUpdateStatus==null){
            int currentVersionCode=getVersionCode();
            fullUpdateStatus=new FullUpdateStatus(currentVersionCode,false);
            try {
                localDataStorage.storeObject(LocalDataStorage.NAME_FULL_UPDATE_STATUS,fullUpdateStatus);
            } catch (IOException e) {
                log.error("",e);
            }
        }

        if(fullUpdateStatus.isUpdated())
            return;

        getView().showProgress();
        getView().showToast("Full updating....");

        fullUpdateStatus.setUpdated(true);
        try {
            localDataStorage.storeObject(LocalDataStorage.NAME_FULL_UPDATE_STATUS,fullUpdateStatus);
        } catch (IOException e) {
            log.error("",e);
        }

        SessionModule sessionModule=new SessionModuleImpl(context);
        sessionModule.getAllSessions()
                .subscribe(new Observer<List<ChatSession>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<ChatSession> chatSessions) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to execute session full update",e);
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("Failed to update session info");
                        }
                    }

                    @Override
                    public void onComplete() {
                        log.info("Session full update finished");
                        if(isViewAttached()){
                            getView().hideProgress();
                        }
                    }
                });

    }
}
