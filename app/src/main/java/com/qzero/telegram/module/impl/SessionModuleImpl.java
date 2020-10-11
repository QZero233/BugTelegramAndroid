package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.exchange.PackedObjectFactory;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.SessionService;
import com.qzero.telegram.module.SessionModule;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class SessionModuleImpl implements SessionModule {

    private Context context;
    private SessionService sessionService;
    private PackedObjectFactory objectFactory=new CommonPackedObjectFactory();

    private ChatSessionDao sessionDao;

    public SessionModuleImpl(Context context) {
        this.context = context;
        RetrofitHelper helper=RetrofitHelper.getInstance(context);
        sessionService=helper.getService(SessionService.class);
        sessionDao=SessionManager.getInstance(context).getSession().getChatSessionDao();
    }

    @Override
    public Observable<List<ChatSession>> getAllSessions() {
        return sessionService.getAllSessions()
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just((List<ChatSession>) packedObject.parseCollectionObject("ChatSessionList",List.class,ChatSession.class)))
                .flatMap(sessionList -> {
                    if(sessionList!=null){
                        for(ChatSession session:sessionList){
                            sessionDao.insertOrReplace(session);
                        }
                    }
                    return Observable.just(sessionList);
                });
    }

    @Override
    public Observable<ChatSession> getSession(String sessionId) {
        return sessionService.getSession(sessionId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ChatSession.class)))
                .flatMap(session -> {
                    sessionDao.insertOrReplace(session);
                    return Observable.just(session);
                });
    }

    @Override
    public Observable<ActionResult> createSession(ChatSession session) {
        PackedObject parameter=objectFactory.getPackedObject();
        parameter.addObject(session);

        return sessionService.createSession(parameter)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)));
    }

    @Override
    public Observable<ActionResult> addChatMember(ChatMember chatMember) {
        PackedObject parameter=objectFactory.getPackedObject();
        parameter.addObject(chatMember);

        return sessionService.addChatMember(chatMember.getSessionId(),parameter)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)));
    }

    @Override
    public Observable<ActionResult> removeChatMember(String sessionId, String memberUserName) {
        return sessionService.removeChatMember(sessionId,memberUserName)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)));
    }

    @Override
    public void deleteSessionLogically(String sessionId) {
        ChatSession session=sessionDao.load(sessionId);
        if(session!=null){
            session.setDeleted(true);
            sessionDao.update(session);
        }
    }

    @Override
    public void deleteSessionPhysically(String sessionId) {
        sessionDao.deleteByKey(sessionId);
    }
}
