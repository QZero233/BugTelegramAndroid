package com.qzero.telegram.notice.processor;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.dao.gen.ChatMemberDao;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.dao.gen.ChatSessionParameterDao;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.MessageModuleImpl;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeAction;
import com.qzero.telegram.notice.bean.NoticeDataType;
import com.qzero.telegram.utils.LocalStorageUtils;
import com.qzero.telegram.utils.UUIDUtils;

import org.greenrobot.greendao.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class SessionNoticeProcessor implements NoticeProcessor {

    private Logger log= LoggerFactory.getLogger(getClass());
    private Context context;

    private SessionModule sessionModule;
    private BroadcastModule broadcastModule;
    private MessageModule messageModule;

    private ChatSessionDao sessionDao;
    private ChatMemberDao memberDao;
    private ChatSessionParameterDao parameterDao;

    public SessionNoticeProcessor(Context context) {
        this.context = context;

        sessionModule=new SessionModuleImpl(context);
        broadcastModule=new BroadcastModuleImpl(context);
        messageModule=new MessageModuleImpl(context);

        sessionDao= SessionManager.getInstance(context).getSession().getChatSessionDao();
        memberDao= SessionManager.getInstance(context).getSession().getChatMemberDao();
        parameterDao=SessionManager.getInstance(context).getSession().getChatSessionParameterDao();
    }

    @Override
    public NoticeDataType getDataType() {
        return NoticeDataType.TYPE_SESSION;
    }

    @Override
    public boolean processNotice(DataNotice notice, NoticeAction action) {
        String sessionId=action.getDataId();
        log.debug(String.format("Processing session update with id %s and action %s", sessionId,action.getActionType()));

        String myName= LocalStorageUtils.getLocalTokenUserName(context);

        ChatSession session;
        switch (action.getActionType()){
            case "newSession":
                sessionModule.getSession(sessionId)
                        .subscribe(o-> {},e -> {log.error("Failed to sync session with id "+sessionId,e);},
                                ()->{broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_INSERT);});
                break;
            case "newMember":
                String newMemberUserName=action.getParameter().get("memberUserName");
                if(newMemberUserName.equals(myName)){
                    //Which means I'm new here, I need to pull session info
                    Observable<ChatSession> sessionObservable= sessionModule.getSession(sessionId);
                    Observable<List<ChatMessage>> messageObservable= messageModule.getAllMessagesBySessionIdRemotely(sessionId);
                    Observable.zip(sessionObservable,messageObservable,(chatSession,message) -> {
                        addSystemNotice(sessionId,notice.getGenerateTime(), String.format("你已被 %s 邀请加入群聊", action.getOperator()));
                        broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_INSERT);
                        return Observable.just(0);
                    }).subscribe(o -> {});
                }else {
                    ChatMember member=new ChatMember(sessionId,newMemberUserName,ChatMember.LEVEL_NORMAL);
                    memberDao.save(member);

                    sessionDao.detachAll();

                    broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE);

                    addSystemNotice(sessionId,notice.getGenerateTime(), String.format("%s 已被 %s 邀请加入群聊", newMemberUserName ,action.getOperator()));
                }

                break;
            case "removeMember":
                String removeMemberUserName=action.getParameter().get("memberUserName");

                if (removeMemberUserName.equals(myName)){
                    //You're kicked out
                    ChatSession sessionDelete=sessionDao.load(sessionId);
                    sessionDelete.setDeleted(true);
                    sessionDao.insertOrReplace(sessionDelete);
                    addSystemNotice(sessionId,notice.getGenerateTime(), String.format("您 已被 %s 移出群聊" ,action.getOperator()));
                    broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_DELETE);
                }else{
                    QueryBuilder queryBuilder=memberDao.queryBuilder();
                    queryBuilder.where(ChatMemberDao.Properties.UserName.eq(removeMemberUserName),ChatMemberDao.Properties.SessionId.eq(sessionId));
                    queryBuilder.buildDelete().executeDeleteWithoutDetachingEntities();
                    broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE);
                    addSystemNotice(sessionId,notice.getGenerateTime(), String.format("%s 已被 %s 移出群聊", removeMemberUserName ,action.getOperator()));
                }

                sessionDao.detachAll();

                break;
            case "updateMemberLevel":
                String updateMemberUserName=action.getParameter().get("memberUserName");
                int newLevel=Integer.parseInt(action.getParameter().get("level"));

                ChatMember member=memberDao.queryBuilder().where(ChatMemberDao.Properties.UserName.eq(updateMemberUserName),
                        ChatMemberDao.Properties.SessionId.eq(sessionId)).unique();
                member.setLevel(newLevel);
                memberDao.save(member);

                broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE);

                String level="普通用户";
                switch (member.getLevel()){
                    case ChatMember.LEVEL_NORMAL:
                        level="普通用户";
                        break;
                    case ChatMember.LEVEL_OPERATOR:
                        level="管理员";
                        break;
                    case ChatMember.LEVEL_OWNER:
                        level="会话拥有者";
                        break;
                }

                addSystemNotice(sessionId,notice.getGenerateTime(), String.format("%s 已被 %s 钦点为 %s", updateMemberUserName ,action.getOperator(),level));

                break;
            case "deleteSession":
                sessionModule.deleteSessionLogically(sessionId);
                broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_DELETE);

                addSystemNotice(sessionId,notice.getGenerateTime(), String.format("会话已被 %s 删除", action.getOperator()));

                break;
            case "updateSessionParameter":
                String parameterName=action.getParameter().get("parameterName");
                String parameterValue=action.getParameter().get("parameterValue");

                ChatSessionParameter parameter=parameterDao.queryBuilder().where(ChatSessionParameterDao.Properties.SessionId.eq(sessionId),
                        ChatSessionParameterDao.Properties.ParameterName.eq(parameterName)).unique();

                if(parameter==null){
                    parameter=new ChatSessionParameter(null,sessionId,parameterName,parameterValue);
                }else{
                    parameter.setParameterValue(parameterValue);
                }
                parameterDao.insertOrReplace(parameter);

                sessionDao.detachAll();

                break;
            case "deleteSessionParameter":
                String parameterNameDelete=action.getParameter().get("parameterName");

                ChatSessionParameter parameterDelete=parameterDao.queryBuilder().where(ChatSessionParameterDao.Properties.SessionId.eq(sessionId),
                        ChatSessionParameterDao.Properties.ParameterName.eq(parameterNameDelete)).unique();

                parameterDao.delete(parameterDelete);
                sessionDao.detachAll();
                break;
            case "addSessionParameter":
                String parameterNameAdd=action.getParameter().get("parameterName");
                String parameterValueAdd=action.getParameter().get("parameterValue");
                parameter=new ChatSessionParameter(null,sessionId,parameterNameAdd,parameterValueAdd);
                parameterDao.insertOrReplace(parameter);

                sessionDao.detachAll();
                break;
        }
        return true;
    }

    private void addSystemNotice(String sessionId,long time,String content){
        ChatMessage message=new ChatMessage(UUIDUtils.getRandomUUID(),null,sessionId,time,null,ChatMessage.TYPE_SYSTEM_NOTICE);
        message.setContent(content.getBytes());

        messageModule.saveLocalSystemNotice(message);

        broadcastModule.sendBroadcast(NoticeDataType.TYPE_MESSAGE,sessionId, BroadcastModule.ActionType.ACTION_TYPE_INSERT);
    }
}
