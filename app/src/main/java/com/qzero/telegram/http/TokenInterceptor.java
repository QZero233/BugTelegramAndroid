package com.qzero.telegram.http;

import android.content.Context;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.factory.LocalDataStorageFactory;
import com.qzero.telegram.http.bean.Token;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

    private Context context;
    private LocalDataStorage localDataStorage;
    private Logger log= LoggerFactory.getLogger(getClass());

    public TokenInterceptor(Context context) {
        this.context = context;
        localDataStorage= LocalDataStorageFactory.getStorage(context);
    }

    @NotNull
    @Override
    public Response intercept(@NotNull Chain chain) throws IOException {
        Token token=localDataStorage.getObject(LocalDataStorage.NAME_LOCAL_TOKEN,Token.class);
        if(token==null){
            log.debug("There is no stored token");
            return chain.proceed(chain.request());
        }else{
            Request request=chain.request();
            request=request.newBuilder().header("token_id",token.getTokenId()).header("owner_user_name",token.getOwnerUserName()).build();
            return chain.proceed(request);
        }
    }
}
