package com.qzero.telegram.dao.factory;

import android.content.Context;

import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.impl.LocalDataStorageImpl;

public class LocalDataStorageFactory {

    public static LocalDataStorage getStorage(Context context){
        return new LocalDataStorageImpl(context);
    }

}
