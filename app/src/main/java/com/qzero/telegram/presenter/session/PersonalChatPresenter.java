package com.qzero.telegram.presenter.session;

import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.gen.ChatMessageDao;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class PersonalChatPresenter extends BaseChatPresenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private ChatMessageDao messageDao;

    @Override
    public void initSessionInfo(String sessionId) {
        super.initSessionInfo(sessionId);
        //Then mark read
        messageDao= SessionManager.getInstance(getView().getContext()).getSession().getChatMessageDao();
        List<ChatMessage> unreadMessageList=messageDao.queryBuilder().where(ChatMessageDao.Properties.SessionId.eq(sessionId),
                ChatMessageDao.Properties.SenderUserName.notEq(myName),
                ChatMessageDao.Properties.MessageStatus.eq("unread")).list();

        if(unreadMessageList!=null && !unreadMessageList.isEmpty()){
            for(ChatMessage message:unreadMessageList){
                markRead(message.getMessageId());
            }
        }
    }

    public void markRead(String messageId) {
        messageModule.updateMessageStatus(messageId,"read")
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to mark message as read with messageId "+messageId,e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void sendMessage(byte[] content,String messageType) {
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMessageStatus("sending");
        chatMessage.setContent(content);
        chatMessage.setSendTime(System.currentTimeMillis());
        chatMessage.setSessionId(session.getSessionId());
        chatMessage.setSenderUserName(myName);
        chatMessage.setMessageType(messageType);

        if(messageList!=null){
            messageList.add(chatMessage);
            getView().showMessageList(messageList);
        }

        chatMessage.setMessageStatus("unread");

        messageModule.sendMessage(chatMessage)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {
                        if(actionResult.isSucceeded() && isViewAttached())
                            getView().clearMessageInput();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to send message "+chatMessage,e);
                        if(isViewAttached()){
                            getView().showToast("发送失败");
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            messageList.get(messageList.size()-1).setMessageStatus("empty");
                            getView().showMessageList(messageList);
                        }
                    }
                });
    }

    @Override
    public void registerMessageBroadcastListener() {
        super.registerMessageBroadcastListener();

        broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_MESSAGE, (dataId, actionType) -> {
            if(actionType!= BroadcastModule.ActionType.ACTION_TYPE_INSERT)
                return;

            ChatMessage message=messageDao.load(dataId);
            if(message.getSessionId().equals(session.getSessionId()) && !message.getMessageType().equals(ChatMessage.TYPE_SYSTEM_NOTICE) && !message.getSenderUserName().equals(myName)){
                //Which means the message was sent by the partner
                log.debug("Received message from partner,try to mark read");
                markRead(dataId);
            }
        });

    }
}
