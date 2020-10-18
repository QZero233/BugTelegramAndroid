package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.entity.UserInfo;
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

import io.reactivex.rxjava3.core.Observable;

public class UserInfoModuleImpl implements UserInfoModule {

    private Logger log= LoggerFactory.getLogger(getClass());

    private Context context;

    private UserInfoService service;

    public UserInfoModuleImpl(Context context) {
        this.context = context;
        service= RetrofitHelper.getInstance(context).getService(UserInfoService.class);
    }

    @Override
    public Observable<UserInfo> getPersonalInfo() {
        LocalDataStorage localDataStorage=new LocalDataStorageImpl(context);

        try {
            UserInfo personalInfo=localDataStorage.getObject(LocalDataStorage.NAME_PERSONAL_INFO,UserInfo.class);
            if(personalInfo!=null)
                return Observable.just(personalInfo);
        }catch (IOException e){
            log.error("Failed to get local personal info",e);
        }

        return service.getPersonalInfo()
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> {
                    UserInfo personalInfo=packedObject.parseObject(UserInfo.class);
                    localDataStorage.storeObject(LocalDataStorage.NAME_PERSONAL_INFO,personalInfo);
                    return Observable.just(personalInfo);
                });
    }

    @Override
    public Observable<ActionResult> updatePersonalInfo(UserInfo newUserInfo) {
        PackedObject parameter=new CommonPackedObjectFactory().getParameter(context);
        parameter.addObject(newUserInfo);

        return service.updatePersonalInfo(parameter)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)));
    }
}
