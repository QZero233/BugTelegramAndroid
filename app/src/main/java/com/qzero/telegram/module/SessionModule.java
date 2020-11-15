package com.qzero.telegram.module;

import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.http.bean.ActionResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface SessionModule {

    Observable<List<ChatSession>> getAllSessions();

    Observable<ChatSession> getSession(String sessionId);

    Observable<ActionResult> createSession(ChatSession session);

    Observable<ActionResult> addChatMember(ChatMember chatMember);

    Observable<ActionResult> removeChatMember(String sessionId,String memberUserName);

    Observable<ActionResult> quitSession(String sessionId);

    Observable<ActionResult> deleteSession(String sessionId);

    Observable<ActionResult> updateSessionName(ChatSession session);

    Observable<ActionResult> updateChatMemberLevel(ChatMember chatMember);

    void deleteSessionPhysically(String sessionId);

    void deleteSessionLogically(String sessionId);

}
