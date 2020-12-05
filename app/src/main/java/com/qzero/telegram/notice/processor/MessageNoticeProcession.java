package com.qzero.telegram.notice.processor;

import android.content.Context;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.gen.ChatMessageDao;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.MessageModuleImpl;
import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeAction;
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

    private ChatMessageDao messageDao;

    public MessageNoticeProcession(Context context) {
        this.context = context;
        messageModule=new MessageModuleImpl(context);
        broadcastModule=new BroadcastModuleImpl(context);

        messageDao= SessionManager.getInstance(context).getSession().getChatMessageDao();
    }

    @Override
    public NoticeDataType getDataType() {
        return NoticeDataType.TYPE_MESSAGE;
    }

    @Override
    public boolean processNotice(DataNotice notice, NoticeAction action) {
        log.debug(String.format("Processing message update with id %s and action %s", action.getDataId(),action.getActionType()));

        String messageId=action.getDataId();

        switch (action.getActionType()){
            case "addMessage":
                messageModule.getMessage(messageId)
                        .subscribe(new Observer<ChatMessage>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull ChatMessage message) {

                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                log.error("Failed to sync message with id "+messageId,e);
                            }

                            @Override
                            public void onComplete() {
                                broadcastModule.sendBroadcast(NoticeDataType.TYPE_MESSAGE,messageId, BroadcastModule.ActionType.ACTION_TYPE_INSERT);
                            }
                        });
                break;
            case "deleteMessage":
                messageModule.deleteMessageLocallyLogically(messageId);
                broadcastModule.sendBroadcast(NoticeDataType.TYPE_MESSAGE,messageId, BroadcastModule.ActionType.ACTION_TYPE_DELETE);
                break;
            case "updateMessageStatus":
                String newStatus=action.getParameter().get("newStatus");
                ChatMessage message=messageDao.load(messageId);
                if(message!=null){
                    message.setMessageStatus(newStatus);
                    broadcastModule.sendBroadcast(NoticeDataType.TYPE_MESSAGE,messageId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE);
                }
                break;
        }

        return true;
    }
}
