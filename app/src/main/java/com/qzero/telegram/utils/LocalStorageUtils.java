package com.qzero.telegram.utils;

import android.content.Context;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;
import com.qzero.telegram.http.bean.Token;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LocalStorageUtils {

    private static Logger log= LoggerFactory.getLogger(LocalStorageUtils.class);

    public static String getLocalTokenUserName(Context context){
        LocalDataStorage localDataStorage=new LocalDataStorageImpl(context);
        try {
            Token token=localDataStorage.getObject(LocalDataStorage.NAME_LOCAL_TOKEN,Token.class);
            return token.getOwnerUserName();
        } catch (Exception e) {
            log.error("Failed to get local token",e);
            throw new RuntimeException("Failed to get local toke");
        }
    }

}
