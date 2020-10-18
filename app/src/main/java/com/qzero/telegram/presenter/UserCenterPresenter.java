package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.UserCenterContract;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.module.UserInfoModule;
import com.qzero.telegram.module.impl.UserInfoModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;


public class UserCenterPresenter extends BasePresenter<UserCenterContract.View> implements UserCenterContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private UserInfoModule userInfoModule;

    @Override
    public void attachView(@NonNull UserCenterContract.View mView) {
        super.attachView(mView);
        userInfoModule=new UserInfoModuleImpl(mView.getContext());
    }

    @Override
    public void loadPersonalInfo() {
        getView().showProgress();
        userInfoModule.getPersonalInfo()
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull UserInfo userInfo) {
                        if(isViewAttached()){
                            getView().showPersonalInfo(userInfo);
                        }
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to get personal info",e);
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("Failed to get your info");
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
