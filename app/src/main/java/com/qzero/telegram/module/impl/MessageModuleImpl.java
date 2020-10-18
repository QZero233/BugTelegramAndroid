package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.gen.ChatMessageDao;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.exchange.PackedObjectFactory;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.MessageService;
import com.qzero.telegram.module.MessageModule;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class MessageModuleImpl implements MessageModule {

    private Context context;
    private MessageService service;

    private ChatMessageDao messageDao;

    private PackedObjectFactory objectFactory=new CommonPackedObjectFactory();

    public MessageModuleImpl(Context context) {
        this.context = context;
        service= RetrofitHelper.getInstance(context).getService(MessageService.class);
        messageDao= SessionManager.getInstance(context).getSession().getChatMessageDao();
    }

    @Override
    public Observable<ChatMessage> getMessage(String messageId) {
        return service.getMessage(messageId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ChatMessage.class)))
                .flatMap(message -> {
                    messageDao.insertOrReplace(message);
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
                        ChatMessage message=messageDao.load(messageId);
                        if(message!=null){
                            message.setMessageStatus("deleted");
                            messageDao.insertOrReplace(message);
                        }
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
                        ChatMessage message=messageDao.load(messageId);
                        if(message!=null){
                            message.setMessageStatus(newStatus);
                            messageDao.insertOrReplace(message);
                        }
                    }
                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<List<ChatMessage>> getAllMessagesBySessionId(String sessionId) {
        return service.getAllMessagesBySessionId(sessionId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just((List<ChatMessage>)packedObject.parseCollectionObject("messageList",List.class,ChatMessage.class)))
                .flatMap(messageList -> {
                    for(ChatMessage message:messageList){
                        messageDao.insertOrReplace(message);
                    }
                    return Observable.just(messageList);
                });
    }
}

