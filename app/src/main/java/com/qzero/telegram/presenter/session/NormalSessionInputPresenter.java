package com.qzero.telegram.presenter.session;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.InputSessionInfoContract;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.SessionModule;
import com.qzero.telegram.module.impl.SessionModuleImpl;
import com.qzero.telegram.presenter.BasePresenter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class NormalSessionInputPresenter extends BasePresenter<InputSessionInfoContract.View> implements InputSessionInfoContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private SessionModule sessionModule;

    @Override
    public void attachView(@NonNull InputSessionInfoContract.View mView) {
        super.attachView(mView);

        sessionModule=new SessionModuleImpl(getView().getContext());
    }

    @Override
    public void submit(List<ChatSessionParameter> parameterList) {
        if(parameterList==null){
            getView().showLocalErrorMessage("传参错误");
            return;
        }

        ChatSession chatSession=new ChatSession();
        chatSession.setSessionParameters(parameterList);
        chatSession.setChatMembers(new ArrayList<>());

        getView().showProgress();
        sessionModule.createSession(chatSession)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {
                        if(isViewAttached()){
                            getView().showToast("创建成功");
                            getView().exit();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to create a new session",e);
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("创建失败");
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
