package com.qzero.telegram.http.error;

import android.content.Context;


import com.qzero.telegram.dao.LocalDataStorage;
import com.qzero.telegram.dao.factory.LocalDataStorageFactory;
import com.qzero.telegram.http.bean.ActionResult;
import com.qzero.telegram.http.exchange.PackedObject;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Function;


public class DefaultErrorHandleFunction implements Function<PackedObject, Observable<PackedObject>> {

    private Logger log= LoggerFactory.getLogger(getClass());
    private Context context;
    private LocalDataStorage  localDataStorage;

    public DefaultErrorHandleFunction(Context context) {
        this.context = context;
        localDataStorage= LocalDataStorageFactory.getStorage(context);
    }

    @Override
    public Observable<PackedObject> apply(PackedObject packedParameter) throws Throwable {
        ActionResult actionResult=packedParameter.parseObject(ActionResult.class);
        if(actionResult==null || !actionResult.isSucceeded()){
            if(actionResult.getStatusCode()==ErrorCodeList.CODE_ILLEGAL_TOKEN){
                localDataStorage.removeObject(LocalDataStorage.NAME_LOCAL_TOKEN);
                log.debug("Wrong token info,client is cleaning local token info stored");
            }
            throw new RemoteActionFailedException(actionResult.getStatusCode(),actionResult.getMessage());
        }
        return Observable.just(packedParameter);
    }
}
