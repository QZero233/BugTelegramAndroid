package com.qzero.telegram.notice.processor;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class SessionNoticeProcessor implements NoticeProcessor {

    private Logger log= LoggerFactory.getLogger(getClass());
    private Context context;
    private SessionModule sessionModule;
    private BroadcastModule broadcastModule;

    public SessionNoticeProcessor(Context context) {
        this.context = context;
        sessionModule=new SessionModuleImpl(context);
        broadcastModule=new BroadcastModuleImpl(context);
    }

    @Override
    public NoticeDataType getDataType() {
        return NoticeDataType.TYPE_SESSION;
    }

    @Override
    public boolean processNotice(DataNotice notice) {
        URI uri=URI.create(notice.getDataUri());
        String dataId=uri.getAuthority();
        String detail=uri.getFragment();
        log.debug(String.format("Processing session update with id %s and detail %s", dataId,detail));


        if(detail!=null && detail.equals("deleted")){
            //SESSION deleted,use logical delete
            //TODO LOGICAL DELETE
            sessionModule.deleteSession(dataId);
            broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,dataId, BroadcastModule.ActionType.ACTION_TYPE_DELETE);
        }else{
            sessionModule.getSession(dataId)
                    .subscribe(new Observer<ChatSession>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull ChatSession session) {

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            log.error("Failed to sync session with id "+dataId,e);
                        }

                        @Override
                        public void onComplete() {
                            broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,dataId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE_OR_INSERT);
                        }
                    });
        }

        return true;
    }
}
