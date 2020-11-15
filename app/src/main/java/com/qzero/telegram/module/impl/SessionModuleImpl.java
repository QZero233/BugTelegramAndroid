package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.gen.ChatMemberDao;
import com.qzero.telegram.dao.gen.ChatMessageDao;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.exchange.PackedObjectFactory;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.SessionService;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.utils.LocalStorageUtils;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class SessionModuleImpl implements SessionModule {

    private Context context;
    private SessionService sessionService;
    private PackedObjectFactory objectFactory=new CommonPackedObjectFactory();

    private ChatSessionDao sessionDao;
    private ChatMemberDao memberDao;

    public SessionModuleImpl(Context context) {
        this.context = context;
        RetrofitHelper helper=RetrofitHelper.getInstance(context);
        sessionService=helper.getService(SessionService.class);

        sessionDao=SessionManager.getInstance(context).getSession().getChatSessionDao();
        memberDao=SessionManager.getInstance(context).getSession().getChatMemberDao();
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
                            memberDao.insertOrReplaceInTx(session.getChatMembers());
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
                    memberDao.insertOrReplaceInTx(session.getChatMembers());
                    return Observable.just(session);
                });
    }

    @Override
    public Observable<ActionResult> createSession(ChatSession session) {
        PackedObject parameter=objectFactory.getPackedObject();
        parameter.addObject(session);

        return sessionService.createSession(parameter)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    return Observable.just(packedObject.parseObject(ActionResult.class));
                });
    }

    @Override
    public Observable<ActionResult> addChatMember(ChatMember chatMember) {
        return sessionService.addChatMember(chatMember.getSessionId(),chatMember.getUserName())
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
    public Observable<ActionResult> quitSession(String sessionId) {
        String myName= LocalStorageUtils.getLocalTokenUserName(context);
        return removeChatMember(sessionId,myName)
                .flatMap(actionResult -> {
                    if(actionResult.isSucceeded()){
                        deleteSessionLogically(sessionId);
                    }

                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<ActionResult> deleteSession(String sessionId) {
        return sessionService.deleteSession(sessionId)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    ActionResult actionResult=packedObject.parseObject(ActionResult.class);

                    if(actionResult.isSucceeded()){
                        deleteSessionLogically(sessionId);
                    }

                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<ActionResult> updateSessionName(ChatSession session) {
        return sessionService.updateSessionName(session.getSessionId(),session.getSessionName())
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    ActionResult actionResult=packedObject.parseObject(ActionResult.class);

                    if(actionResult.isSucceeded()){
                        sessionDao.insertOrReplace(session);
                    }

                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<ActionResult> updateChatMemberLevel(ChatMember chatMember) {
        PackedObjectFactory objectFactory=new CommonPackedObjectFactory();
        PackedObject parameter=objectFactory.getParameter(context);
        parameter.addObject(chatMember);
        return sessionService.updateChatMemberLevel(chatMember.getUserName(),chatMember.getSessionId(),chatMember.getLevel())
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                   ActionResult  actionResult=packedObject.parseObject(ActionResult.class);

                   if(actionResult.isSucceeded()){
                       ChatSession session=sessionDao.load(chatMember.getSessionId());
                       List<ChatMember> memberList=session.getChatMembers();
                       for(int i=0;i<memberList.size();i++){
                           if(memberList.get(i).getUserName().equals(chatMember.getUserName())){
                               memberList.set(i,chatMember);
                               break;
                           }
                       }
                       sessionDao.insertOrReplace(session);
                   }

                   return Observable.just(actionResult);
                });
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
        ChatMessageDao messageDao=SessionManager.getInstance(context).getSession().getChatMessageDao();
        messageDao.queryBuilder().where(ChatMessageDao.Properties.SessionId.eq(sessionId)).buildDelete().executeDeleteWithoutDetachingEntities();
    }
}
