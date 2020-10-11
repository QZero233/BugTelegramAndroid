package com.qzero.telegram.notice;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.qzero.telegram.module.NoticeModule;
import com.qzero.telegram.module.bean.NoticeConnectInfo;
import com.qzero.telegram.module.impl.NoticeModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class NoticeMonitorService extends Service {

    private Logger log= LoggerFactory.getLogger(getClass());

    private NoticeMonitorThread thread;
    private boolean occupied=false;

    private NoticeModule module;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        module=new NoticeModuleImpl(this);
        startMonitorThread();
    }

    public synchronized void startMonitorThread(){
        if(occupied)
            return;//Another connection request is running

        if(thread!=null && thread.isConnectionAlive())
            return;//Already started

        module.requestConnection()
                .subscribe(new Observer<NoticeConnectInfo>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        occupied=true;
                    }

                    @Override
                    public void onNext(@NonNull NoticeConnectInfo connectInfo) {
                        log.debug("Got monitor connection info "+connectInfo);
                        thread=new NoticeMonitorThread(NoticeMonitorService.this,connectInfo);
                        thread.start();
                        occupied=false;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        occupied=false;
                        log.error("Failed to request notice monitor connection",e);
                        log.debug("Trying after 5 sec");
                        try {
                            Thread.sleep(5*1000);
                        }catch (Exception e1){
                        }

                        startMonitorThread();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

}
