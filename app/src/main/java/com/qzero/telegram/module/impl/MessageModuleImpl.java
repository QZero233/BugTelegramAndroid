package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.MessageContentManager;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.gen.ChatMessageDao;
import com.qzero.telegram.dao.impl.MessageContentManagerImpl;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.exchange.PackedObjectFactory;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.MessageService;
import com.qzero.telegram.module.MessageModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class MessageModuleImpl implements MessageModule {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Context context;
    private MessageService service;

    private ChatMessageDao messageDao;
    private MessageContentManager contentManager;

    private PackedObjectFactory objectFactory=new CommonPackedObjectFactory();

    public MessageModuleImpl(Context context) {
        this.context = context;
        service= RetrofitHelper.getInstance(context).getService(MessageService.class);
        messageDao= SessionManager.getInstance(context).getSession().getChatMessageDao();
        contentManager=new MessageContentManagerImpl(context);
    }

    @Override
    public Observable<ChatMessage> getMessage(String messageId) {
        return service.getMessage(messageId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ChatMessage.class)))
                .flatMap(message -> {
                    messageDao.insertOrReplace(message);
                    contentManager.saveMessageContent(message);
                    return Observable.just(message);
                });
    }

    @Override
    public Observable<ActionResult> saveMessage(ChatMessage message) {
        PackedObject parameter=objectFactory.getParameter(context);
        parameter.addObject(message);
        return service.saveMessage(parameter)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)))
                .flatMap(actionResult -> {
                    if(actionResult.isSucceeded()){
                        messageDao.insertOrReplace(message);
                        contentManager.saveMessageContent(message);
                    }
                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<ActionResult> deleteMessage(String messageId) {
        return service.deleteMessage(messageId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)))
                .flatMap(actionResult -> {
                    if(actionResult.isSucceeded()){
                        deleteMessageLocallyLogically(messageId);
                    }
                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<ActionResult> updateMessageStatus(String messageId, String newStatus) {
        PackedObject parameter=objectFactory.getParameter(context);
        parameter.addObject("messageStatus",newStatus);
        return service.updateMessageStatus(messageId,parameter)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)))
                .flatMap(actionResult -> {
                    if(actionResult.isSucceeded()){
                        updateMessageStatusLocally(messageId,newStatus);
                    }
                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<List<ChatMessage>> getAllMessagesBySessionId(String sessionId) {
        List<ChatMessage> messageListLocal=messageDao.queryBuilder().where(ChatMessageDao.Properties.SessionId.eq(sessionId)).list();
        if(messageListLocal==null || messageListLocal.isEmpty()) {
            return service.getAllMessagesBySessionId(sessionId)
                    .compose(DefaultTransformer.getInstance(context))
                    .flatMap(packedObject -> Observable.just((List<ChatMessage>) packedObject.parseCollectionObject("messageList", List.class, ChatMessage.class)))
                    .flatMap(messageList -> {
                        for (ChatMessage message : messageList) {
                            messageDao.insertOrReplace(message);
                            contentManager.saveMessageContent(message);
                        }
                        return Observable.just(messageList);
                    });
        }else{
            for(ChatMessage message:messageListLocal){
                try {
                    message.setContent(contentManager.getMessageContent(message.getMessageId()));
                } catch (IOException e) {
                    log.error("Failed to get message content with message id "+message.getMessageId(),e);
                }
            }
            return Observable.just(messageListLocal);
        }

    }

    private void updateMessageStatusLocally(String messageId,String newStatus){
        ChatMessage message=messageDao.load(messageId);
        if(message!=null){
            message.setMessageStatus(newStatus);
            messageDao.insertOrReplace(message);
        }
    }

    @Override
    public void deleteMessageLocallyLogically(String messageId) {
        updateMessageStatusLocally(messageId,"deleted");
    }

    @Override
    public void deleteMessageLocallyPhysically(String messageId) {
        ChatMessage message=messageDao.load(messageId);
        if(message!=null){
            messageDao.delete(message);
            try {
                contentManager.deleteMessageContent(messageId);
            } catch (IOException e) {
                log.error("Failed to delete local message content with message id "+messageId,e);
            }
        }
    }

    @Override
    public void deleteAllMessagesLocally() {
        List<ChatMessage> messageList=messageDao.loadAll();
        messageDao.deleteAll();
        for(ChatMessage message:messageList){
            try {
                contentManager.deleteMessageContent(message.getMessageId());
            } catch (IOException e) {
                log.error("Failed to delete local message content with message id "+message.getMessageId(),e);
            }
        }
    }
}

