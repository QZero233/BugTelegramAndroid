package com.qzero.telegram.notice.processor;

import android.content.Context;

import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.module.SessionModule;
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

    public SessionNoticeProcessor(Context context) {
        this.context = context;
        sessionModule=new SessionModuleImpl(context);
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

        /*sessionModule.getSession(dataId)
                .subscribe(new Observer<ChatSession>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull ChatSession session) {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });*/

        //TODO PROCESS SESSION

        return false;
    }
}
