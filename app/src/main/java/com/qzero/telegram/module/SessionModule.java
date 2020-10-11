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

    void deleteSession(String sessionId);

}
