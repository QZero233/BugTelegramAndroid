package com.qzero.telegram.presenter.session;

import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.MessageModule;
import com.qzero.telegram.module.impl.MessageModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class PersonalChatPresenter extends BaseChatPresenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private MessageModule messageModule;

    public PersonalChatPresenter(String sessionId) {
        super(sessionId);
    }

    public void markRead(String messageId) {
        if(messageModule==null)
            messageModule=new MessageModuleImpl(getView().getContext());
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
    public void onNewMessageArrive(String messageId) {
        super.onNewMessageArrive(messageId);
        markRead(messageId);
    }
}
