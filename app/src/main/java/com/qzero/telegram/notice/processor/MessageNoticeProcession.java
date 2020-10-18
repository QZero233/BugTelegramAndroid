package com.qzero.telegram.notice.processor;

import android.content.Context;

import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.MessageModuleImpl;
import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class MessageNoticeProcession implements NoticeProcessor {

    private Context context;
    private Logger log= LoggerFactory.getLogger(getClass());

    private MessageModule messageModule;
    private BroadcastModule broadcastModule;

    public MessageNoticeProcession(Context context) {
        this.context = context;
        messageModule=new MessageModuleImpl(context);
        broadcastModule=new BroadcastModuleImpl(context);
    }

    @Override
    public NoticeDataType getDataType() {
        return NoticeDataType.TYPE_MESSAGE;
    }

    @Override
    public boolean processNotice(DataNotice notice) {
        URI uri=URI.create(notice.getDataUri());
        String dataId=uri.getAuthority();
        String detail=uri.getFragment();

        log.debug(String.format("Processing message update with id %s and detail %s", dataId,detail));

        if(detail!=null && detail.equals("deleted")) {
            //message deleted,use logical delete
            messageModule.deleteMessageLocallyLogically(dataId);
            broadcastModule.sendBroadcast(NoticeDataType.TYPE_MESSAGE,dataId, BroadcastModule.ActionType.ACTION_TYPE_DELETE);
        }else{
            messageModule.getMessage(dataId)
                    .subscribe(new Observer<ChatMessage>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@NonNull ChatMessage message) {

                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            log.error("Failed to sync message with id "+dataId,e);
                        }

                        @Override
                        public void onComplete() {
                            broadcastModule.sendBroadcast(NoticeDataType.TYPE_MESSAGE,dataId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE_OR_INSERT);
                        }
                    });
        }
        return true;
    }
}
