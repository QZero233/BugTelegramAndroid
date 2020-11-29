package com.qzero.telegram.presenter.session;

import com.qzero.telegram.module.MessageModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonalChatPresenter extends BaseChatPresenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private MessageModule messageModule;

    public PersonalChatPresenter(String sessionId) {
        super(sessionId);
    }

    public void markRead(String messageId) {
        /*if(messageModule==null)
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
                });*/

        //FIXME
        //这样会出事
        //A标记已读，更新消息状态，B会收到更新通知，并调用该方法，标记已读
        //然后A收到消息又会标记已读，陷入循环
    }

    @Override
    public void onNewMessageArrive(String messageId) {
        super.onNewMessageArrive(messageId);
        markRead(messageId);
    }
}
