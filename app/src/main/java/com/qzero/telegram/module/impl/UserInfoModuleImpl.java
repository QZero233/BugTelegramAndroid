package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.SessionManager;
import com.qzero.telegram.dao.entity.UserInfo;
import com.qzero.telegram.dao.gen.UserInfoDao;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.http.service.UserInfoService;
import com.qzero.telegram.module.UserInfoModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

public class UserInfoModuleImpl implements UserInfoModule {

    private Logger log = LoggerFactory.getLogger(getClass());

    private Context context;

    private UserInfoService service;

    private UserInfoDao userInfoDao;

    public UserInfoModuleImpl(Context context) {
        this.context = context;
        service = RetrofitHelper.getInstance(context).getService(UserInfoService.class);
        userInfoDao = SessionManager.getInstance(context).getSession().getUserInfoDao();
    }

    @Override
    public Observable<ActionResult> updatePersonalInfo(UserInfo newUserInfo) {
        PackedObject parameter = new CommonPackedObjectFactory().getParameter(context);
        parameter.addObject(newUserInfo);

        return service.updatePersonalInfo(parameter)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject ->
                {
                    ActionResult actionResult=packedObject.parseObject(ActionResult.class);

                    if(actionResult.isSucceeded()){
                        userInfoDao.insertOrReplace(newUserInfo);
                    }

                    return Observable.just(actionResult);
                });
    }

    @Override
    public Observable<UserInfo> getUserInfo(String userName) {

        UserInfo localUserInfo=userInfoDao.queryBuilder().where(UserInfoDao.Properties.UserName.eq(userName)).unique();

        Observable<UserInfo> remote=service.getOtherUserInfo(userName)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject ->
                {
                    UserInfo userInfo=packedObject.parseObject(UserInfo.class);
                    userInfoDao.insertOrReplace(userInfo);
                    return Observable.just(userInfo);
                });

        if(localUserInfo==null)
            return remote;
        else
            return Observable.concat(Observable.just(localUserInfo),remote);
    }

    @Override
    public Observable<UserInfo> getUserInfoFromOnlyRemote(String userName) {
        return service.getOtherUserInfo(userName)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject ->
                {
                    UserInfo userInfo=packedObject.parseObject(UserInfo.class);
                    userInfoDao.insertOrReplace(userInfo);
                    return Observable.just(userInfo);
                });
    }

    @Override
    public List<UserInfo> getLocalFriendList() {
        return userInfoDao.loadAll();
    }

    @Override
    public void deleteLocally(String userName) {
        userInfoDao.deleteByKey(userName);
    }
}
