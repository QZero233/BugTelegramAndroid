package com.qzero.telegram.presenter.session;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.ChatContract;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.ChatMessage;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.gen.ChatSessionDao;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.BroadcastModule;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.impl.BroadcastModuleImpl;
import com.qzero.telegram.module.impl.MessageModuleImpl;
import com.qzero.telegram.notice.bean.NoticeDataType;
import com.qzero.telegram.presenter.BasePresenter;
import com.qzero.telegram.utils.LocalStorageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class BaseChatPresenter extends BasePresenter<ChatContract.View> implements ChatContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    protected MessageModule messageModule;
    protected BroadcastModule broadcastModule;

    protected ChatSessionDao sessionDao;

    protected ChatSession session;

    protected String myName;

    protected List<ChatMessage> messageList;

    @Override
    public void attachView(@NonNull ChatContract.View mView) {
        super.attachView(mView);
        myName=LocalStorageUtils.getLocalTokenUserName(getView().getContext());
    }

    @Override
    public void loadMessageList() {
        messageList=messageModule.getAllMessagesBySessionIdLocally(session.getSessionId());
        getView().showMessageList(messageList);
    }

    @Override
    public void sendMessage(byte[] content) {
        ChatMessage chatMessage=new ChatMessage();
        chatMessage.setMessageStatus("sending");
        chatMessage.setContent(content);
        chatMessage.setSendTime(System.currentTimeMillis());
        chatMessage.setSessionId(session.getSessionId());
        chatMessage.setSenderUserName(myName);

        loadMessageList();

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
                            messageList.get(messageList.size()-1).setMessageStatus("empty");
                            getView().showMessageList(messageList);
                        }
                    }
                });
    }

    @Override
    public void initSessionInfo(String sessionId) {
        messageModule=new MessageModuleImpl(getView().getContext());
        broadcastModule=new BroadcastModuleImpl(getView().getContext());

        sessionDao= SessionManager.getInstance(getView().getContext()).getSession().getChatSessionDao();

        session=sessionDao.load(sessionId);
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
                            loadMessageList();
                        }
                    }
                });
    }

    @Override
    public void deleteMessage(String messageId, boolean isPhysical) {
        if(isPhysical){
            messageModule.deleteMessageLocallyPhysically(messageId);
            loadMessageList();
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
            loadMessageList();
        });

        broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_SESSION,((dataId, actionType) -> {
            if(!isViewAttached())
                return;

            if(actionType== BroadcastModule.ActionType.ACTION_TYPE_DELETE){
                getView().showDeletedMode();
            }else{
                session=sessionDao.load(session.getSessionId());
                getView().loadSessionInfo(session);
            }

        }));
    }

    @Override
    public void unregisterMessageBroadcastListener() {
        broadcastModule.unregisterAllReceivers();
    }
}
