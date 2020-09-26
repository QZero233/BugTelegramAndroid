package com.qzero.telegram.module.impl;

import android.content.Context;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.factory.LocalDataStorageFactory;
import com.qzero.telegram.http.RetrofitHelper;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.bean.Token;
import com.qzero.telegram.http.exchange.CommonPackedObjectFactory;
import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.exchange.PackedObjectFactory;
import com.qzero.telegram.http.service.AuthorizeService;
import com.qzero.telegram.http.service.DefaultTransformer;
import com.qzero.telegram.module.AuthorizeModule;
import com.qzero.telegram.module.bean.LoginForm;

import java.io.IOException;

import io.reactivex.rxjava3.core.Observable;

public class AuthorizeModuleImpl implements AuthorizeModule {

    private Context context;
    private AuthorizeService authorizeService;
    private PackedObjectFactory objectFactory=new CommonPackedObjectFactory();
    private LocalDataStorage localDataStorage;

    public AuthorizeModuleImpl(Context context) {
        this.context = context;
        authorizeService= RetrofitHelper.getInstance(context).getService(AuthorizeService.class);
        localDataStorage= LocalDataStorageFactory.getStorage(context);
    }

    @Override
    public Observable<ActionResult> login(LoginForm loginForm, Token tokenPreset) {
        PackedObject packedObject=objectFactory.getPackedObject();

        packedObject.addObject("loginUserInfo",loginForm);
        packedObject.addObject("tokenPreset",tokenPreset);

        return authorizeService.login(packedObject)
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(returnValue -> {
                    Token token=returnValue.parseObject("TokenEntity",Token.class);
                    localDataStorage.storeObject(LocalDataStorage.NAME_LOCAL_TOKEN,token);
                    return Observable.just(returnValue.parseObject(ActionResult.class));
                });
    }

    @Override
    public Observable<ActionResult> logout() throws IOException {
        Token token=localDataStorage.getObject(LocalDataStorage.NAME_LOCAL_TOKEN,Token.class);

        return authorizeService.logout(token.getTokenId())
                .compose(DefaultTransformer.getInstance(context))
                .flatMap(packedObject -> Observable.just(packedObject.parseObject(ActionResult.class)));
    }
}
