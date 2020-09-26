package com.qzero.telegram.service;

import android.content.Context;

public class RetrofitHelper {

    private static RetrofitHelper instance;
    private Context context;



    private RetrofitHelper(Context context) {
        this.context = context;
    }

    public static RetrofitHelper getInstance(Context context) {
        if(instance==null)
            instance=new RetrofitHelper(context);
        return instance;
    }


}
