package com.qzero.telegram.presenter.session;

import android.text.TextUtils;

import com.qzero.telegram.contract.InputSessionInfoContract;
import com.qzero.telegram.dao.entity.ChatMember;
import com.qzero.telegram.dao.entity.ChatSession;
import com.qzero.telegram.dao.entity.ChatSessionParameter;
import com.qzero.telegram.http.bean.ActionResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class PersonalSessionInfoInputPresenter extends BaseSessionInfoInputPresenter implements InputSessionInfoContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private String dstUserName=null;

    @Override
    public void submit(List<ChatSessionParameter> parameterList) {
        if(parameterList==null){
            getView().showLocalErrorMessage("传参错误");
            return;
        }

        for(int i=0;i<parameterList.size();i++){
            ChatSessionParameter parameter=parameterList.get(i);
            if(parameter.getParameterName().equals("dstUserName")){
                dstUserName=parameter.getParameterValue();
                parameterList.remove(i);
                break;
            }
        }

        if(TextUtils.isEmpty(dstUserName)){
            getView().showLocalErrorMessage("对方用户名不能为空");
            return;
        }



        parameterList.add(new ChatSessionParameter(null,null,ChatSessionParameter.NAME_SESSION_TYPE,ChatSessionParameter.SESSION_TYPE_PERSONAL));

        ChatSession chatSession=new ChatSession();
        chatSession.setSessionParameters(parameterList);
        chatSession.setChatMembers(new ArrayList<>());

        getView().showProgress();
        sessionModule.createSession(chatSession)
                .flatMap(actionResult -> {
                   return sessionModule.addChatMember(new ChatMember(actionResult.getMessage(),dstUserName,ChatMember.LEVEL_NORMAL));
                })
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
