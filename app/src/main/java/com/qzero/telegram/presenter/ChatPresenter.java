package com.qzero.telegram.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.ChatContract;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.MessageModuleImpl;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class ChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private SessionModule sessionModule;
    private MessageModule messageModule;
    private BroadcastModule broadcastModule;

    private ChatSessionDao sessionDao;

    private Context context;

    private String sessionId;

    private List<ChatMessage> messageList;

    public ChatPresenter(String sessionId) {
        this.sessionId = sessionId;
    }

    @Override
    public void attachView(@NonNull ChatContract.View mView) {
        super.attachView(mView);
        context=mView.getContext();

        sessionModule=new SessionModuleImpl(context);
        messageModule=new MessageModuleImpl(context);
        broadcastModule=new BroadcastModuleImpl(context);

        sessionDao= SessionManager.getInstance(mView.getContext()).getSession().getChatSessionDao();
    }

    @Override
    public void loadSessionInfo(String sessionId) {
        ChatSession session=sessionDao.load(sessionId);
        if(session==null){
            log.error(String.format("Can not find session with id %s locally", sessionId));
            getView().showToast("错误，本地会话信息不存在");
            return;
        }

        if(session.isDeleted()){
            getView().showDeletedMode();
        }

        getView().loadSessionInfo(session);
    }

    @Override
    public void loadMessageList(String sessionId) {
        messageList=messageModule.getAllMessagesBySessionIdLocally(sessionId);
        getView().showMessageList(messageList);
    }

    @Override
    public void sendMessage(String senderName,byte[] content) {
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMessageStatus("sending");
        chatMessage.setContent(content);
        chatMessage.setSendTime(System.currentTimeMillis());
        chatMessage.setSessionId(sessionId);
        chatMessage.setSenderUserName(senderName);

        if(messageList!=null){
            messageList.add(chatMessage);
            getView().showMessageList(messageList);
        }

        chatMessage.setMessageStatus("empty");

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
                            messageList.get(messageList.size()-1).setMessageStatus("unread");
                            getView().showMessageList(messageList);
                        }
                    }
                });
    }

    @Override
    public void updateMessageStatus(String messageId, String newStatus) {
        getView().showProgress();
        messageModule.updateMessageStatus(messageId,newStatus)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to update message status with messageId "+messageId,e);
                        if(isViewAttached()){
                            getView().hideProgress();
                        }
                    }

                    @Override
                    public void onComplete() {
                        log.debug(String.format("Updated message status to %s with id %s", newStatus,messageId));
                        if(isViewAttached()){
                            getView().hideProgress();
                            loadMessageList(sessionId);
                        }
                    }
                });
    }


    //Do it in two-way session
    /*@Override
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
    }*/

    @Override
    public void deleteMessage(String messageId, boolean isPhysical) {
        if(isPhysical){
            messageModule.deleteMessageLocallyPhysically(messageId);
            loadMessageList(sessionId);
        }else{
            getView().showProgress();
            messageModule.deleteMessage(messageId)
                    .subscribe(new Observer<ActionResult>() {
                        @Override
                        public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                        }

                        @Override
                        public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                            log.error("Failed to delete remote message with id "+messageId,e);
                            if(isViewAttached()){
                                getView().hideProgress();
                                getView().showToast("删除失败");
                            }
                        }

                        @Override
                        public void onComplete() {
                            if(isViewAttached()){
                                getView().hideProgress();
                                //loadMessageList(sessionId);
                            }
                        }
                    });
        }
    }

    @Override
    public void registerMessageBroadcastListener() {
        broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_MESSAGE, (dataId, actionType) -> {
            if(!isViewAttached())
                return;
            loadMessageList(sessionId);
        });

        broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_SESSION,((dataId, actionType) -> {
            if(!isViewAttached())
                return;

            if(actionType== BroadcastModule.ActionType.ACTION_TYPE_DELETE){
                getView().showDeletedMode();
            }else{
                ChatSession session=sessionDao.load(sessionId);
                getView().loadSessionInfo(session);
            }

        }));
    }

    @Override
    public void unregisterMessageBroadcastListener() {
        broadcastModule.unregisterAllReceivers();
    }

    @Override
    public String getSessionName() {
        return sessionModule.getSessionParameterLocally(sessionId, ChatSessionParameter.NAME_SESSION_NAME);
    }
}
