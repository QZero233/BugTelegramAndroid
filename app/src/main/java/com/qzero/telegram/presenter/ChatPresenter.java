package com.qzero.telegram.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.ChatContract;
import com.qzero.telegram.dao.entity.ChatMessage;
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
        getView().showProgress();

        messageModule.getAllMessagesBySessionId(sessionId)
                .subscribe(new Observer<List<ChatMessage>>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull List<ChatMessage> messageList) {
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
    public void registerMessageBroadcastListener() {
        broadcastModule.registerReceiverForCertainData(NoticeDataType.TYPE_MESSAGE, (dataId, actionType) -> loadMessageList(sessionId));
    }

    @Override
    public void unregisterMessageBroadcastListener() {
        broadcastModule.unregisterAllReceivers();
    }
}
