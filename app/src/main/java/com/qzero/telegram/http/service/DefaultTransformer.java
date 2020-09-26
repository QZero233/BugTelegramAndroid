package com.qzero.telegram.http.service;

import android.content.Context;


import com.qzero.telegram.http.exchange.PackedObject;
import com.qzero.telegram.http.error.DefaultErrorHandleFunction;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableSource;
import io.reactivex.rxjava3.core.ObservableTransformer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DefaultTransformer implements ObservableTransformer<PackedObject,PackedObject> {

    private static DefaultTransformer instance;
    private Context context;

    public static DefaultTransformer getInstance(Context context) {
        if(instance==null)
            instance=new DefaultTransformer(context);
        return instance;
    }

    private DefaultTransformer(Context context) {
        this.context = context;
    }

    @Override
    public @NonNull ObservableSource<PackedObject> apply(@NonNull Observable<PackedObject> upstream) {
        return upstream.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new DefaultErrorHandleFunction(context));
    }
}
