package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.LoginContract;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.error.ErrorCodeList;
import com.qzero.telegram.http.error.RemoteActionFailedException;
import com.qzero.telegram.module.AuthorizeModule;
import com.qzero.telegram.module.bean.LoginForm;
import com.qzero.telegram.module.impl.AuthorizeModuleImpl;
import com.qzero.telegram.utils.SHA256Utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class LoginPresenter extends BasePresenter<LoginContract.View> implements LoginContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private AuthorizeModule authorizeModule;

    @Override
    public void attachView(@NonNull LoginContract.View mView) {
        super.attachView(mView);

        authorizeModule=new AuthorizeModuleImpl(mView.getContext());
    }

    @Override
    public void login(String userName, String password) {
        String passwordHash= SHA256Utils.getHexEncodedSHA256(password);
        LoginForm loginForm=new LoginForm(userName,passwordHash);

        getView().showProgress();

        authorizeModule.login(loginForm)
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {
                        if(isViewAttached()){
                            getView().showToast("登录成功，正在跳转至用户中心.....");
                            getView().jumpToUserCenter();
                            getView().exit();
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Login failed",e);
                        if(isViewAttached()){
                            getView().hideProgress();
                            if(e instanceof RemoteActionFailedException){
                                int code=((RemoteActionFailedException) e).getErrorCode();
                                switch (code){
                                    case ErrorCodeList.CODE_WRONG_LOGIN_INFO:
                                        getView().showToast("登录信息错误");
                                        break;
                                    default:
                                        getView().showToast("登录失败，错误信息为"+((RemoteActionFailedException) e).getRemoteMessage());
                                        break;
                                }

                            }
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
