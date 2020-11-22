package com.qzero.telegram.module;

import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.http.bean.ActionResult;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public interface MessageModule {

    Observable<ChatMessage> getMessage(String messageId);

    Observable<ActionResult> sendMessage(ChatMessage message);

    Observable<ActionResult> deleteMessage(String messageId);

    Observable<ActionResult> updateMessageStatus(String messageId,String newStatus);

    Observable<List<ChatMessage>> getAllMessagesBySessionIdRemotely(String sessionId);

    List<ChatMessage> getAllMessagesBySessionIdLocally(String sessionId);

    void deleteMessageLocallyLogically(String messageId);

    void deleteMessageLocallyPhysically(String messageId);

    void deleteAllMessagesLocally();

    void saveLocalSystemNotice(ChatMessage message);

}
