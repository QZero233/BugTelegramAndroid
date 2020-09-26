package com.qzero.telegram.http;

import android.content.Context;

import com.qzero.telegram.R;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitHelper {

    public String serverBaseUrl;

    private static RetrofitHelper instance;
    private Context context;

    private Retrofit mRetrofit;

    private RetrofitHelper(Context context) {
        this.context = context;
        initRetrofit();
    }

    public static RetrofitHelper getInstance(Context context) {
        if(instance==null)
            instance=new RetrofitHelper(context);
        return instance;
    }

    private void initRetrofit(){
        serverBaseUrl= context.getString(R.string.server_base_url);

        OkHttpClient client=new OkHttpClient.Builder()
                .addNetworkInterceptor(new TokenInterceptor(context))
                .build();

        mRetrofit=new Retrofit.Builder()
                .baseUrl(serverBaseUrl)
                .addConverterFactory(JacksonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .client(client)
                .build();
    }

    public <T> T getService(Class<T> cls) {
        return mRetrofit.create(cls);
    }

}
