package com.qzero.telegram.http.service;

import com.qzero.telegram.http.exchange.PackedObject;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;

public interface UserInfoService {

    @GET("/account/user_info")
    Observable<PackedObject> getPersonalInfo();

    @PUT("/account/user_info")
    Observable<PackedObject> updatePersonalInfo(@Body PackedObject parameter);

}
