package com.qzero.telegram.notice.processor;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.gen.ChatMemberDao;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.notice.bean.DataNotice;
import com.qzero.telegram.notice.bean.NoticeAction;
import com.qzero.telegram.notice.bean.NoticeDataType;
import com.qzero.telegram.utils.LocalStorageUtils;

import org.greenrobot.greendao.query.QueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class SessionNoticeProcessor implements NoticeProcessor {

    private Logger log= LoggerFactory.getLogger(getClass());
    private Context context;
    private SessionModule sessionModule;
    private BroadcastModule broadcastModule;

    private ChatSessionDao sessionDao;
    private ChatMemberDao memberDao;

    public SessionNoticeProcessor(Context context) {
        this.context = context;
        sessionModule=new SessionModuleImpl(context);
        broadcastModule=new BroadcastModuleImpl(context);

        sessionDao= SessionManager.getInstance(context).getSession().getChatSessionDao();
        memberDao= SessionManager.getInstance(context).getSession().getChatMemberDao();
    }

    @Override
    public NoticeDataType getDataType() {
        return NoticeDataType.TYPE_SESSION;
    }

    @Override
    public boolean processNotice(DataNotice notice, NoticeAction action) {
        String sessionId=action.getDataId();
        log.debug(String.format("Processing session update with id %s and action %s", sessionId,action.getActionType()));

        ChatSession session;
        switch (action.getActionType()){
            case "newSession":
                sessionModule.getSession(sessionId)
                        .subscribe(null,e -> {log.error("Failed to sync session with id "+sessionId,e);},
                                ()->{broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE_OR_INSERT);});
                break;
            case "newMember":
                String newMemberUserName=action.getParameter().get("memberUserName");
                String myName= LocalStorageUtils.getLocalTokenUserName(context);
                if(newMemberUserName.equals(myName)){
                    //Which means I'm new here, I need to pull session info
                    sessionModule.getSession(sessionId)
                            .subscribe(null,e -> {log.error("Failed to sync session with id "+sessionId,e);},
                                    ()->{broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE_OR_INSERT);});
                }else {
                    ChatMember member=new ChatMember(sessionId,newMemberUserName,ChatMember.LEVEL_NORMAL);
                    memberDao.save(member);

                    broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE_OR_INSERT);
                }

                break;
            case "removeMember":
                String removeMemberUserName=action.getParameter().get("memberUserName");

                QueryBuilder queryBuilder=memberDao.queryBuilder();
                queryBuilder.where(ChatMemberDao.Properties.UserName.eq(removeMemberUserName),ChatMemberDao.Properties.SessionId.eq(sessionId));
                queryBuilder.buildDelete().executeDeleteWithoutDetachingEntities();

                broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE_OR_INSERT);

                break;
            case "updateMemberLevel":
                String updateMemberUserName=action.getParameter().get("memberUserName");
                int newLevel=Integer.parseInt(action.getParameter().get("level"));

                session=sessionDao.load(sessionId);
                List<ChatMember> memberList2=session.getChatMembers();

                for(int i=0;i<memberList2.size();i++){
                    ChatMember member=memberList2.get(i);
                    if(member.getUserName().equals(updateMemberUserName)){
                        member.setLevel(newLevel);
                        memberList2.set(i,member);
                        break;
                    }
                }
                sessionDao.insertOrReplace(session);
                break;
            case "deleteSession":
                sessionModule.deleteSessionLogically(sessionId);
                broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_DELETE);
                break;
            case "updateSessionName":
                String newName=action.getParameter().get("name");

                session=sessionDao.load(sessionId);
                session.setSessionName(newName);
                sessionDao.insertOrReplace(session);

                broadcastModule.sendBroadcast(NoticeDataType.TYPE_SESSION,sessionId, BroadcastModule.ActionType.ACTION_TYPE_UPDATE_OR_INSERT);
                break;
        }


        return true;
    }
}
