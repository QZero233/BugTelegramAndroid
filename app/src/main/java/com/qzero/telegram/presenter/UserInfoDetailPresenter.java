package com.qzero.telegram.presenter;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.UserInfoDetailContract;
import com.qzero.telegram.module.UserInfoModule;
import com.qzero.telegram.module.impl.UserInfoModuleImpl;

public class UserInfoDetailPresenter extends BasePresenter<UserInfoDetailContract.View> implements UserInfoDetailContract.Presenter {

    private UserInfoModule userInfoModule;

    @Override
    public void attachView(@NonNull UserInfoDetailContract.View mView) {
        super.attachView(mView);
        userInfoModule=new UserInfoModuleImpl(mView.getContext());
    }

    @Override
    public void loadUserInfoDetail(String userName) {
        userInfoModule.getUserInfo(userName)
                .subscribe(userInfo -> {
                    if(isViewAttached()){
                        getView().loadUserInfoDetail(userInfo);
                    }
                });
    }

    @Override
    public void delete(String userName) {
        userInfoModule.deleteLocally(userName);
        getView().showToast("删除成功");
        getView().exit();
    }
}
