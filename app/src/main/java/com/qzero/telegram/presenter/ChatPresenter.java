package com.qzero.telegram.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.ChatContract;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.MessageModuleImpl;
import com.qzero.telegram.notice.bean.NoticeDataType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class ChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private MessageModule messageModule;
    private BroadcastModule broadcastModule;

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

        messageModule=new MessageModuleImpl(context);
        broadcastModule=new BroadcastModuleImpl(context);
    }

    @Override
    public void loadMessageList(String sessionId) {
        if(isViewAttached())
            getView().showProgress();

        messageModule.getAllMessagesBySessionId(sessionId)
                .subscribe(new Observer<List<ChatMessage>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<ChatMessage> messageList) {
                        ChatPresenter.this.messageList=messageList;
                        if(isViewAttached()){
                            getView().showMessageList(messageList);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("获取聊天记录失败");
                        }
                        log.error("Failed to get chat messages",e);
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                        }
                    }
                });
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
                            messageList.get(messageList.size()-1).setMessageStatus("unread");
                            getView().showMessageList(messageList);
                        }
                    }
                });
    }

    @Override
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
        broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_MESSAGE, (dataId, actionType) -> loadMessageList(sessionId));
    }

    @Override
    public void unregisterMessageBroadcastListener() {
        broadcastModule.unregisterAllReceivers();
    }
}
