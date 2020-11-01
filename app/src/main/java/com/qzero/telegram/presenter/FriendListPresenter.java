package com.qzero.telegram.presenter;

import android.content.Context;

import androidx.annotation.NonNull;

import com.qzero.telegram.contract.FriendListContract;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.http.error.ErrorCodeList;
import com.qzero.telegram.http.error.RemoteActionFailedException;
import com.qzero.telegram.module.UserInfoModule;
import com.qzero.telegram.module.impl.UserInfoModuleImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.Disposable;

public class FriendListPresenter extends BasePresenter<FriendListContract.View> implements FriendListContract.Presenter {

    private Logger log= LoggerFactory.getLogger(getClass());

    private UserInfoModule userInfoModule;

    @Override
    public void attachView(@NonNull FriendListContract.View mView) {
        super.attachView(mView);
        userInfoModule=new UserInfoModuleImpl(mView.getContext());
    }

    @Override
    public void findUser(String userName) {
        getView().showProgress();
        userInfoModule.getUserInfoFromOnlyRemote(userName)
                .subscribe(new Observer<UserInfo>() {
                    @Override
                    public void onSubscribe(@io.reactivex.rxjava3.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@io.reactivex.rxjava3.annotations.NonNull UserInfo userInfo) {
                        loadLocalFriendList();
                    }

                    @Override
                    public void onError(@io.reactivex.rxjava3.annotations.NonNull Throwable e) {
                        log.error("Failed to get remote user info",e);
                        if(isViewAttached()){
                            getView().hideProgress();

                            if(e instanceof RemoteActionFailedException){
                                if(((RemoteActionFailedException) e).getErrorCode() == ErrorCodeList.CODE_MISSING_RESOURCE){
                                    getView().showToast("用户不存在");
                                }
                            }else{
                                getView().showToast("添加失败");
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

    @Override
    public void loadLocalFriendList() {
        getView().loadLocalFriendList(userInfoModule.getLocalFriendList());
    }
}
