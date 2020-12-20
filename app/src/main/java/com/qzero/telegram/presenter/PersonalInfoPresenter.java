package com.qzero.telegram.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.PersonalInfoContract;
import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.module.UserInfoModule;
import com.qzero.telegram.module.impl.UserInfoModuleImpl;
import com.qzero.telegram.utils.LocalStorageUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class PersonalInfoPresenter extends BasePresenter<PersonalInfoContract.View> implements PersonalInfoContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Context context;

    private UserInfoModule userInfoModule;

    @Override
    public void attachView(@NonNull PersonalInfoContract.View mView) {
        super.attachView(mView);
        context=mView.getContext();
        userInfoModule=new UserInfoModuleImpl(context);
    }

    @Override
    public void loadPersonalInfo() {
        try {
            UserInfo userInfo=userInfoModule.getUserInfoLocally(LocalStorageUtils.getLocalTokenUserName(context));
            getView().loadPersonalInfo(userInfo);
        }catch (Exception e){
            log.error("Failed to get local personal info to show",e);
            getView().showToast("获取本地个人信息失败");
            getView().disableSubmitButton();
        }
    }

    @Override
    public void updatePersonalInfo(UserInfo userInfo) {
        getView().showProgress();
        userInfoModule.updateAccountStatusAndMotto(userInfo.getAccountStatus(),userInfo.getMotto())
                .subscribe(new Observer<ActionResult>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull ActionResult actionResult) {

                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("更改失败");
                            getView().gotoMainFragmentAndReloadInfo(null);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(isViewAttached()){
                            getView().hideProgress();
                            getView().showToast("更改成功");
                            getView().gotoMainFragmentAndReloadInfo(userInfo);

                            try {
                                LocalDataStorage localDataStorage=new LocalDataStorageImpl(context);
                                localDataStorage.storeObject(LocalDataStorage.NAME_PERSONAL_INFO,userInfo);
                            }catch (IOException e){
                                log.error("Failed to save personal info updated",e);
                            }

                        }
                    }
                });
    }
}
